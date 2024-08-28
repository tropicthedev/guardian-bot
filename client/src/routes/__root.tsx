import { createRootRoute, Outlet } from '@tanstack/react-router'
import { useLocation } from "@tanstack/react-router";
import Header from '../components/ui/header'

import '../index.css'

export const Route = createRootRoute({
    component: Root
})


function Root() {
    const location = useLocation()

    const isLoginPage = location.pathname === '/'

    return (
        <div className='mb-5'>
            {isLoginPage ? <></> : <Header />}
            <Outlet />
            {/* <TanStackRouterDevtools /> */}
        </div>
    )
}
