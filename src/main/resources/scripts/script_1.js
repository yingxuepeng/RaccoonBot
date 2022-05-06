var DURATION = 86400 * 1000 * 30;
var MAX_MSG_CNT = 80;
function shouldMute(dataStr) {
    var data = JSON.parse(dataStr);
    var now = new Date().getTime();
    var result = { shouldMute: false, msgCnt: 0, msgLimitCnt: MAX_MSG_CNT - (data.actionList.length - 1) * 10 };
    for (var index = 0; index < data.msgTimeList.length; index++) {
        var time = data.msgTimeList[index];
        if (time + DURATION > now) {
            result.msgCnt++;
        }
    }
    if (result.msgCnt > result.msgLimitCnt) {
        var dayMillis = 86400 * 1000;
        result.muteMillis = dayMillis - (now % dayMillis);
        result.shouldMute = true;
    }
    return JSON.stringify(result);
}
