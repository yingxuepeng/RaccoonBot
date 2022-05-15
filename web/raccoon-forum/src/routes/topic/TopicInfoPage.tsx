import axios from "axios";
import { useEffect } from "react";
import { useCallback } from "react";
import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import { TopicInfoResponse } from "../../network/model/topicInfoResponse";

interface TopicInfoState {
  topicKey: string;
  topicInfoResp?: TopicInfoResponse;
}
export default function TopicInfoPage() {
  const [searchParams] = useSearchParams();
  const topicKey = searchParams.get("topicKey");
  const [state, updateState] = useState<TopicInfoState>({ topicKey });

  // console.log(topicKey);
  const getTopicData = useCallback(async () => {
    const resp = await axios.get<TopicInfoResponse>("/api/topic/info", {
      params: {
        topicKey,
      },
    });
    const data = resp.data;
    if (data.topic == undefined) {
      return;
    }
    updateState({ topicInfoResp: data, topicKey });
  }, []);
  useEffect(() => {
    getTopicData();
  }, [getTopicData]);

  // let topicKey = props.params.match.topicKey;
  const renderTitle = () => {
    if (state.topicInfoResp) {
      return state.topicInfoResp.topic.title;
    }
    return "Loading...";
  };

  const renderMsg = () => {
    if (state.topicInfoResp) {
      return (
        <div>
          {state.topicInfoResp.msgList.map((msg) => {
            return <div>{msg.senderNick + "：" + msg.content}</div>;
          })}
        </div>
      );
    }
    return null;
  };

  return (
    <main style={{ padding: "1rem 0" }}>
      <div>{renderTitle()}</div>
      <div>{renderMsg()}</div>
    </main>
  );
}
