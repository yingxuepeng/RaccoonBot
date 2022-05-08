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
const DURATION = 86400 * 1000;
const MAX_MSG_CNT = 300;
function shouldMute(dataStr: string): string {
  let data: Sc1Data = JSON.parse(dataStr);
  let now = new Date();
  let nowTime = now.getTime();
  var today = new Date();
  today.setHours(0, 0, 0, 0);

  let msgLimitCnt = MAX_MSG_CNT;
  if (data.actionList.length > 0) {
    msgLimitCnt -= data.actionList.length * 10;
  }
  let result: Result = { shouldMute: false, msgCnt: 0, msgLimitCnt };
  for (let index = 0; index < data.msgTimeList.length; index++) {
    const time = Number(data.msgTimeList[index]);
    if (time + DURATION > nowTime) {
      result.msgCnt++;
    }
  }
  if (result.msgCnt > result.msgLimitCnt) {
    let dayMillis = 86400 * 1000;
    result.muteMillis = dayMillis - (nowTime - today.getTime());
    result.shouldMute = true;
  }
  return JSON.stringify(result);
}
