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
}
interface Sc1Data {
  msgTimeList: number[];
  actionList: AdminAction[];
}

interface Result {
  shouldMute: boolean;
  muteMillis?: number;
  msgCnt: number;
  msgLimitCnt: number;
}
const DURATION = 86400 * 1000 * 30;
const MAX_MSG_CNT = 80;
function shouldMute(dataStr: string): string {
  let data: Sc1Data = JSON.parse(dataStr);
  let now = new Date().getTime();
  let result: Result = { shouldMute: false, msgCnt: 0, msgLimitCnt: MAX_MSG_CNT - (data.actionList.length - 1) * 10 };
  for (let index = 0; index < data.msgTimeList.length; index++) {
    const time = Number(data.msgTimeList[index]);
    if (time + DURATION > now) {
      result.msgCnt++;
    }
  }
  if (result.msgCnt > result.msgLimitCnt) {
    let dayMillis = 86400 * 1000;
    result.muteMillis = dayMillis - (now % dayMillis);
    result.shouldMute = true;
  }
  return JSON.stringify(result);
}
