// quota calc
// const
var BASE_QUOTA = 150;
var MIN_QUOTA = 20;
/**
 *
 * @param dataStr: {
 * msgBriefList, // [MsgBrief]
 * duration, // mute duration
 * adminCnt //mute admin count
 * }
 * @returns should mute
 */
function shouldMute(dataStr) {
    var data = JSON.parse(dataStr);
    // change quota
    var msgQuota = BASE_QUOTA;
    for (var index = 0; index < data.actionList.length; index++) {
        var action = data.actionList[index];
        msgQuota += action.quotaCnt * action.quotaStep;
    }
    if (msgQuota < MIN_QUOTA) {
        msgQuota = MIN_QUOTA;
    }
    var result = { shouldMute: false, msgCnt: data.msgBriefList.length, msgQuota: msgQuota };
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
