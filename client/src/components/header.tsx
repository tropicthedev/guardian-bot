import { useLocation } from "@tanstack/react-router";

export default function Header() {
    const location = useLocation()
    const isPlayers = location.pathname === '/players';
    const isApplications = location.pathname === '/applications';
    const isInterviews = location.pathname === '/interviews';
    const isServers = location.pathname === '/servers';

    return (
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
                        <li className={`${isPlayers ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/players">Players</a></li>
                        <li>
                            <a>Onboarding</a>
                            <ul className="p-2">
                                <li className={`${isApplications ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/applications">Applications</a></li>
                                <li className={`${isInterviews ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/interviews">Interviews</a></li>
                            </ul>
                        </li>
                        <li className={`${isServers ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/servers">Servers</a></li>
                    </ul>
                </div>
                <a className="btn btn-ghost text-xl">Server Name Here</a>
            </div>
            <div className="navbar-center hidden lg:flex">
                <ul className="menu menu-horizontal px-1 gap-2">
                    <li className={`${isPlayers ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/players">Players</a></li>
                    <li>
                        <details>
                            <summary className={`${isApplications || isInterviews ? 'bg-blue-900 rounded-xl' : ''}`}>Onboarding</summary>
                            <ul className="p-2">
                                <li className={`${isApplications ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/applications">Applications</a></li>
                                <li className={`${isInterviews ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/interviews">Interviews</a></li>
                            </ul>
                        </details>
                    </li>
                    <li className={`${isServers ? 'bg-blue-900 rounded-xl' : ''}`}><a href="/servers">Servers</a></li>
                </ul>
            </div>
            <div className="navbar-end">
                <div className="dropdown dropdown-end">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                        <div className="w-10 rounded-full">
                            <img
                                alt="Tailwind CSS Navbar component"
                                src="https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp" />
                        </div>
                    </div>
                    <ul
                        tabIndex={0}
                        className="menu menu-sm dropdown-content bg-base-100 rounded-box z-[1] mt-3 w-52 p-2 shadow">
                        <li>
                            <a className="justify-between">
                                Profile
                                <span className="badge">New</span>
                            </a>
                        </li>
                        <li><a>Settings</a></li>
                        <li><a>Logout</a></li>
                    </ul>
                </div>
            </div>
        </div>
    );
}
