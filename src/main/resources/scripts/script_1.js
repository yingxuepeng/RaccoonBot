var DURATION = 86400 * 1000;
var MAX_MSG_CNT = 300;
function shouldMute(dataStr) {
    var data = JSON.parse(dataStr);
    var now = new Date();
    var nowTime = now.getTime();
    var today = new Date();
    today.setHours(0, 0, 0, 0);
    var msgLimitCnt = MAX_MSG_CNT;
    if (data.actionList.length > 0) {
        msgLimitCnt -= data.actionList.length * 10;
    }
    var result = { shouldMute: false, msgCnt: 0, msgLimitCnt: msgLimitCnt };
    for (var index = 0; index < data.msgTimeList.length; index++) {
        var time = Number(data.msgTimeList[index]);
        if (time + DURATION > nowTime) {
            result.msgCnt++;
        }
    }
    if (result.msgCnt > result.msgLimitCnt) {
        var dayMillis = 86400 * 1000;
        result.muteMillis = dayMillis - (nowTime - today.getTime());
        result.shouldMute = true;
    }
    return JSON.stringify(result);
}
