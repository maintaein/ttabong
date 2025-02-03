import { useState } from 'react'
import reactLogo from './assets/react.svg'
import './App.css'
import StatusCheck from "./StatusCheck"
function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h4>Vite + React</h4>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
      </div>
      <StatusCheck /> {/* ✅ 백엔드 상태 확인 컴포넌트 추가 */}
    </>
  )
}

export default App
