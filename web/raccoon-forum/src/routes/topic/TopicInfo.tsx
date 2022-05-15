import { useSearchParams } from "react-router-dom";

export default function TopicInfo() {
  const [searchParams] = useSearchParams();
  const topicKey = searchParams.get("topicKey");
  console.log(topicKey);
  // let topicKey = props.params.match.topicKey;
  return (
    <main style={{ padding: "1rem 0" }}>
      <h2>TOPIC INFO</h2>
    </main>
  );
}
