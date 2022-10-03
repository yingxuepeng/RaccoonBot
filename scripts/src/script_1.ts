// quota calc

interface Sc1Data {
  msgBriefList: MsgBrief[];
  actionList: AdminAction[];
  msgConfig: MsgConfig;
}
interface AdminAction {
  adminId: number;
  memberId: number;
  type: number;
  quotaCnt: number;
  quotaStep: number;
}
interface MsgBrief {
  labelType?: number;
  labelFirst?: string;
  labelSecond?: string;
}
interface MsgConfig {
  isWeekend?: boolean;
  isHoliday?: boolean;
}
// result
interface QuotaResult {
  shouldMute: boolean;
  muteMillis?: number;
  msgCnt: number;
  msgQuota: number;
}
// quota const
const BASE_QUOTA = 100;
const MIN_QUOTA = 10;
const WEEKEND_QUOTA_MULTIPLIER = 2;
const HOLIDAY_QUOTA_MULTIPLIER = 3;

// classifier const
const CLASS_REPEAT = '复读';
const CLASS_REPEAT_SCORE = 1.5;

const CLASS_TECH = '科技';
const CLASS_TECH_SCORE = 0.5;
/**
 *
 * @param dataStr: {
 * msgBriefList, // [MsgBrief]
 * actionList, //[AdminAction]
 * }
 * @returns should mute
 */
function shouldMute(dataStr: string): string {
  let data: Sc1Data = JSON.parse(dataStr);

  // change quota
  let msgQuota = BASE_QUOTA;

  for (let actionIdx = 0; actionIdx < data.actionList.length; actionIdx++) {
    const action = data.actionList[actionIdx];
    msgQuota += action.quotaCnt * action.quotaStep;
  }
  if (msgQuota < MIN_QUOTA) {
    msgQuota = MIN_QUOTA;
  }
  if (data.msgConfig.isHoliday) {
    msgQuota *= HOLIDAY_QUOTA_MULTIPLIER;
  } else if (data.msgConfig.isWeekend) {
    msgQuota *= WEEKEND_QUOTA_MULTIPLIER;
  }

  let msgCnt = 0;
  for (let briefIdx = 0; briefIdx < data.msgBriefList.length; briefIdx++) {
    const msg = data.msgBriefList[briefIdx];
    if (msg.labelFirst == CLASS_REPEAT) {
      msgCnt += CLASS_REPEAT_SCORE;
    } else if (msg.labelFirst == CLASS_TECH) {
      msgCnt += CLASS_TECH_SCORE;
    } else {
      msgCnt++;
    }
  }
  let result: QuotaResult = { shouldMute: false, msgCnt: Math.floor(msgCnt), msgQuota };

  if (result.msgCnt >= result.msgQuota) {
    let now = new Date();
    let nowTime = now.getTime();
    var today = new Date();
    today.setHours(0, 0, 0, 0);
    let dayMillis = 86400 * 1000;
    result.muteMillis = dayMillis - (nowTime - today.getTime());
    result.shouldMute = true;
  }
  return JSON.stringify(result);
}
