// quota calc
// quota const
var BASE_QUOTA = 100;
var MIN_QUOTA = 10;
var HOLIDAY_QUOTA_MULTIPLIER = 3;
// classifier const
var CLASS_REPEAT = '复读';
var CLASS_REPEAT_SCORE = 1.5;
var CLASS_TECH = '科技';
var CLASS_TECH_SCORE = 0.5;
/**
 *
 * @param dataStr: {
 * msgBriefList, // [MsgBrief]
 * actionList, //[AdminAction]
 * }
 * @returns should mute
 */
function shouldMute(dataStr) {
    var data = JSON.parse(dataStr);
    // change quota
    var msgQuota = BASE_QUOTA;
    for (var actionIdx = 0; actionIdx < data.actionList.length; actionIdx++) {
        var action = data.actionList[actionIdx];
        msgQuota += action.quotaCnt * action.quotaStep;
    }
    if (msgQuota < MIN_QUOTA) {
        msgQuota = MIN_QUOTA;
    }
    if (data.msgConfig.isHoliday) {
        msgQuota *= HOLIDAY_QUOTA_MULTIPLIER;
    }
    var msgCnt = 0;
    for (var briefIdx = 0; briefIdx < data.msgBriefList.length; briefIdx++) {
        var msg = data.msgBriefList[briefIdx];
        if (msg.labelFirst == CLASS_REPEAT) {
            msgCnt += CLASS_REPEAT_SCORE;
        }
        else if (msg.labelFirst == CLASS_TECH) {
            msgCnt += CLASS_TECH_SCORE;
        }
        else {
            msgCnt++;
        }
    }
    var result = { shouldMute: false, msgCnt: Math.floor(msgCnt), msgQuota: msgQuota };
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
