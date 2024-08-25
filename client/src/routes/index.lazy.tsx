import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/')({
    component: Index,
})

function Index() {
    return (
        <div className="bg-no-repeat bg-cover bg-center relative ">
            <div className="absolute bg-gradient-to-b from-green-500 to-green-400 opacity-75 inset-0 z-0"></div>
            <div className="min-h-screen sm:flex sm:flex-row mx-0 justify-center">
                <div className="flex-col flex  self-center p-10 sm:max-w-5xl xl:max-w-2xl  z-10">
                    <div className="self-start hidden lg:flex flex-col  text-white">
                        <img src="" className="mb-3" />
                        <h1 className="mb-3 font-bold text-5xl">Welcome to Guardian </h1>
                        <p className="pr-3">Guardian is a Discord bot designed to simplify the management of Minecraft server applications, interviews, and whitelist management.</p>
                    </div>
                </div>
                <div className="flex justify-center self-center  z-10">
                    <div className="p-12 bg-base-200 mx-auto rounded-2xl w-100 ">
                        <div className="mb-4">
                            <h3 className="font-semibold text-2xl text-gray-100">Sign In </h3>
                            <p className="text-gray-300">Please sign in to your account.</p>
                        </div>

                        <div className="space-y-5">
                            <button type="submit" className="w-full flex justify-center bg-blue-700  hover:bg-blue-500 text-gray-100 p-3  rounded-full tracking-wide font-semibold  shadow-lg cursor-pointer transition ease-in duration-500">
                                Login With Discord
                            </button>
                            <div className="divider"></div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium text-gray-300 tracking-wide">Email</label>
                                <input className=" w-full text-base px-4 py-2 border  border-gray-300 rounded-lg focus:outline-none focus:border-green-400" type="" placeholder="mail@gmail.com" />
                            </div>
                            <div className="space-y-2">
                                <label className="mb-5 text-sm font-medium text-gray-300 tracking-wide">
                                    Password
                                </label>
                                <input className="w-full content-center text-base px-4 py-2 border  border-gray-300 rounded-lg focus:outline-none focus:border-green-400" type="" placeholder="Enter your password" />
                            </div>
                            <div className="flex items-center justify-between">
                                <div className="flex items-center">
                                    <input id="remember_me" name="remember_me" type="checkbox" className="h-4 w-4 bg-blue-500 focus:ring-blue-400 border-gray-300 rounded" />
                                    <label htmlFor="remember_me" className="ml-2 block text-sm text-gray-300">
                                        Remember me
                                    </label>
                                </div>
                                <div className="text-sm">
                                    <a href="#" className="text-green-400 hover:text-green-500">
                                        Forgot your password?
                                    </a>
                                </div>
                            </div>
                            <div>
                                <button type="submit" className="w-full flex justify-center bg-green-400  hover:bg-green-500 text-gray-100 p-3  rounded-full tracking-wide font-semibold  shadow-lg cursor-pointer transition ease-in duration-500">
                                    Sign in
                                </button>
                            </div>
                        </div>
                        <div className="pt-5 text-center text-gray-400 text-xs">
                            <span>
                                Copyright © 2021-2022
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
