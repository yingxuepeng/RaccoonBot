// quota calc

interface Sc1Data {
  msgBriefList: MsgBrief[];
  actionList: AdminAction[];
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
// result
interface QuotaResult {
  shouldMute: boolean;
  muteMillis?: number;
  msgCnt: number;
  msgQuota: number;
}
// const
const BASE_QUOTA = 150;
const MIN_QUOTA = 20;

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

  for (let index = 0; index < data.actionList.length; index++) {
    const action = data.actionList[index];
    msgQuota += action.quotaCnt * action.quotaStep;
  }
  if (msgQuota < MIN_QUOTA) {
    msgQuota = MIN_QUOTA;
  }
  let result: QuotaResult = { shouldMute: false, msgCnt: data.msgBriefList.length, msgQuota };

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
