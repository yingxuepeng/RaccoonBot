/**
 *
 * @param {
 * msgTimeList, // time in millisec
 * duration, // mute duration
 * adminCnt //mute admin count
 * } data
 * @returns should mute
 */
interface AdminAction {
  adminId: number;
  memberId: number;
  type: number;
  quotaCnt: number;
  quotaStep: number;
}
interface Sc1Data {
  msgTimeList: number[];
  actionList: AdminAction[];
}

interface Result {
  shouldMute: boolean;
  muteMillis?: number;
  msgCnt: number;
  msgQuota: number;
}
const DURATION = 86400 * 1000;
const BASE_QUOTA = 150;
function shouldMute(dataStr: string): string {
  let data: Sc1Data = JSON.parse(dataStr);

  // change quota
  let msgQuota = BASE_QUOTA;
  for (let index = 0; index < data.actionList.length; index++) {
    const action = data.actionList[index];
    msgQuota += action.quotaCnt * action.quotaStep;
  }
  let result: Result = { shouldMute: false, msgCnt: data.msgTimeList.length, msgQuota };

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
