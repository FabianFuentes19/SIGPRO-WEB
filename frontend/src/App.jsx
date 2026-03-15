import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Login from './components/Login'
import DashProyectos from './components/DashProyectos'
function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>
        {/* Comento las rutas para probarlas */}
      {/*<Login/>*/}
      <DashProyectos/>
      </div>
    </>
  )
}


export default App
