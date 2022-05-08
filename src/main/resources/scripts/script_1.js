var DURATION = 86400 * 1000;
var BASE_QUOTA = 150;
function shouldMute(dataStr) {
    var data = JSON.parse(dataStr);
    // change quota
    var msgQuota = BASE_QUOTA;
    for (var index = 0; index < data.actionList.length; index++) {
        var action = data.actionList[index];
        msgQuota += action.quotaCnt * msgQuota;
    }
    var result = { shouldMute: false, msgCnt: data.msgTimeList.length, msgQuota: msgQuota };
    if (result.msgCnt >= result.msgQuota) {
        var now = new Date();
        var nowTime = now.getTime();
        var today = new Date();
        today.setHours(0, 0, 0, 0);
        var dayMillis = 86400 * 1000;
        result.muteMillis = dayMillis - (nowTime - today.getTime());
        result.shouldMute = true;
    }
    return JSON.stringify(result);
}
