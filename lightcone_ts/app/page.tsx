import Image from "next/image";
import Link from 'next/link';

export default function Home() {
  console.log("MUTHER FUCKING TOKEN",localStorage.getItem('token'));
  return (
    <div>
      <h1>Welcome to My App</h1>
      <Link href="/dashboard">
        Go to Dashboard
      </Link>
    </div>
  );
}
