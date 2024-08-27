import { useLocation, Link } from "@tanstack/react-router";

export default function Header() {
    const location = useLocation()
    const isPlayers = location.pathname === '/players';
    const isApplications = location.pathname === '/applications';
    const isServers = location.pathname === '/servers';

    const serverName = "Tropics Server"
    const currentUser = "Tropic"

    return (
        <div>
            <div className="navbar bg-base-100">
                <div className="navbar-start">
                    <div className="dropdown">
                        <div tabIndex={0} role="button" className="btn btn-ghost lg:hidden">
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                className="h-5 w-5"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor">
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="2"
                                    d="M4 6h16M4 12h8m-8 6h16" />
                            </svg>
                        </div>
                        <ul
                            tabIndex={0}
                            className="menu menu-sm dropdown-content bg-base-100 rounded-box z-[1] mt-3 w-52 p-2 shadow">
                            <li className={`${isPlayers ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}>
                                <Link to="/players">Players</Link>
                            </li>
                            <li className={`${isApplications ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}>
                                <Link to="/applications">Applications</Link>
                            </li>
                            <li className={`${isServers ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}>
                                <Link to="/servers">
                                    Servers
                                </Link>
                            </li>
                        </ul>
                    </div>

                    <a className="btn btn-ghost text-xl">
                        <div tabIndex={0} role="button" className="avatar">
                            <div className="w-10 rounded-full">
                                <img
                                    alt={serverName}
                                    src="https://cdn.discordapp.com/icons/1048945862551818310/82de8248952122686c6f89eb092db897.webp" />
                            </div>
                        </div>
                        {serverName}
                    </a>
                </div>
                <div className="navbar-center hidden lg:flex">
                    <ul className="menu menu-horizontal px-1 gap-2">
                        <li className={`${isPlayers ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}><a href="/players">Players</a></li>
                        <li className={`${isApplications ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}><a href="/applications">Applications</a></li>
                        <li className={`${isServers ? 'bg-blue-900 rounded-xl' : ''} text-lg font-semibold`}><a href="/servers">Servers</a></li>
                    </ul>
                </div>
                <div className="navbar-end">
                    <div className="dropdown dropdown-end">
                        <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                            <div className="w-10 rounded-full">
                                <img
                                    alt={currentUser}
                                    src="https://cdn.discordapp.com/avatars/203121159393247232/6239725d6f05f1e6ddfc8ba1c3c26520?size=1024" />
                            </div>
                        </div>
                        <ul
                            tabIndex={0}
                            className="menu menu-sm dropdown-content bg-base-100 rounded-box z-[1] mt-3 w-52 p-2 shadow">
                            <li><a>Logout</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
}
