import { useState } from 'react'
import './App.css'
import Login from './components/Login'
import DashProyectos from './components/DashProyectos'
import DashboardLider from "./components/Dashboards/DashboardLider.jsx";
function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>
          {/* Comento las rutas para probarlas */}
          {/*<Login/>*/}
      <DashboardLider/>
      </div>
    </>
  )
}


export default App
