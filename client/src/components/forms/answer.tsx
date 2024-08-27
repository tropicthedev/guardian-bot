import { useState } from 'react';
import { HiOutlineCheck, HiOutlineBan, HiOutlineX } from "react-icons/hi";
import { ScrollArea } from '../ui/scroll-area';

interface Answer {
    id: number;
    question: string;
    answer: string;
}

export default function AnswerForm() {
    // @ts-expect-error This is temporary
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [answers, setAnswers] = useState<Answer[]>([
        {
            "id": 1,
            "question": "What is your IGN?",
            "answer": "My IGN is MinecraftFan123."
        },
        {
            "id": 2,
            "question": "Preferred Name",
            "answer": "You can call me Alex."
        },
        {
            "id": 3,
            "question": "Preferred Pronouns",
            "answer": "My preferred pronouns are they/them."
        },
        {
            "id": 4,
            "question": "Age",
            "answer": "I am 24 years old."
        },
        {
            "id": 5,
            "question": "Location/Timezone",
            "answer": "I live in New York, so my timezone is EST (Eastern Standard Time)."
        },
        {
            "id": 6,
            "question": "What's your availability like?",
            "answer": "I’m usually available in the evenings from 6 PM to 10 PM on weekdays and most of the day on weekends."
        },
        {
            "id": 7,
            "question": "How did you hear about us? *Please be specific!*",
            "answer": "I heard about CatCraft from a friend who plays on the server."
        },
        {
            "id": 8,
            "question": "How long have you been playing Minecraft?",
            "answer": "I have been playing Minecraft for over 8 years."
        },
        {
            "id": 9,
            "question": "What platform do you play on? Bedrock or Java",
            "answer": "I play on the Java edition of Minecraft."
        },
        {
            "id": 10,
            "question": "What can you bring to a Minecraft server?",
            "answer": "I enjoy building and I’m great at organizing community events."
        },
        {
            "id": 11,
            "question": "Why did you pick CatCraft?",
            "answer": "I picked CatCraft because I love the friendly community and the unique server features."
        },
        {
            "id": 12,
            "question": "Mayonnaise or Miracle Whip?",
            "answer": "Definitely Mayonnaise."
        },
        {
            "id": 13,
            "question": "Any other fun facts we should know? (This is the final question)",
            "answer": "I’m a huge cat lover, and I have a cat named Whiskers who often watches me play Minecraft!"
        }
    ]);
    // @ts-expect-error This is temporary
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [denyReason, setDenyReason] = useState<string>('');
    const [customReason, setCustomReason] = useState<string>('');
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

    const handleAccept = () => {
        // Handle the accept action for the entire application here
    };

    const handleBan = () => {
        // Handle the ban action for the entire application here
    };

    const handleDeny = () => {
        setIsModalOpen(true);
    };

    const handleDenySubmit = () => {
        // Handle the deny action with the reason
        setIsModalOpen(false);
    };

    return (
        <div className="flex flex-col justify-center items-center p-4 max-w-full mx-auto">
            <h1 className="text-3xl font-semibold mb-6">User Application</h1>
            <div className="flex items-center space-x-4 mb-6">
                <div className="avatar">
                    <div className="w-16 rounded-full">
                        <img src="https://cdn.discordapp.com/avatars/203121159393247232/6239725d6f05f1e6ddfc8ba1c3c26520?size=1024" alt="User Name" />
                    </div>
                </div>
                <span className="text-2xl font-semibold">John Doe</span>
            </div>
            <ScrollArea className="h-[80vh] max-h-[600px] mb-4">
                <ul className="space-y-6">
                    {answers.map((answer) => (
                        <li key={answer.id} className="border p-4 rounded-lg shadow-sm">
                            <p className="text-lg"><strong>Question:</strong> {answer.question}</p>
                            <p className="text-lg mt-2"><strong>Answer:</strong> {answer.answer}</p>
                        </li>
                    ))}
                </ul>
            </ScrollArea>
            <div className="flex space-x-2">
                <button
                    onClick={handleAccept}
                    className="btn btn-success flex items-center space-x-1 text-white text-lg"
                >
                    <HiOutlineCheck />
                    <span>Accept</span>
                </button>
                <button
                    onClick={handleBan}
                    className="btn btn-error flex items-center space-x-1 text-white  text-lg"
                >
                    <HiOutlineBan />
                    <span>Ban</span>
                </button>
                <button
                    onClick={handleDeny}
                    className="btn btn-warning flex items-center space-x-1 text-white  text-lg"
                >
                    <HiOutlineX />
                    <span>Deny</span>
                </button>
            </div>

            {isModalOpen && (
                <>
                    <input type="checkbox" id="deny-modal" className="modal-toggle" checked={isModalOpen} readOnly />
                    <div className="modal">
                        <div className="modal-box">
                            <h2 className="text-2xl font-semibold mb-4">Deny Application</h2>
                            <p className="text-lg mb-4">Select a reason for denying this application:</p>
                            <div className="form-control space-y-2">
                                <label className="cursor-pointer flex items-center">
                                    <input
                                        type="checkbox"
                                        name="reason"
                                        value="Spam"
                                        disabled={!!customReason}
                                        onChange={(e) => setDenyReason(e.target.value)}
                                        className="checkbox"
                                    />
                                    <span className="ml-2">Spam</span>
                                </label>
                                <label className="cursor-pointer flex items-center">
                                    <input
                                        type="checkbox"
                                        name="reason"
                                        value="Inappropriate content"
                                        disabled={!!customReason}
                                        onChange={(e) => setDenyReason(e.target.value)}
                                        className="checkbox"
                                    />
                                    <span className="ml-2">Inappropriate content</span>
                                </label>
                                <label className="cursor-pointer flex items-center">
                                    <input
                                        type="checkbox"
                                        name="reason"
                                        value="Other"
                                        disabled={!!customReason}
                                        onChange={(e) => setDenyReason(e.target.value)}
                                        className="checkbox"
                                    />
                                    <span className="ml-2">Other</span>
                                </label>
                                <label className="cursor-pointer flex items-center">
                                    <input
                                        type="text"
                                        placeholder="Custom reason..."
                                        value={customReason}
                                        onChange={(e) => {
                                            setCustomReason(e.target.value);
                                            setDenyReason('');
                                        }}
                                        className="input input-bordered w-full"
                                    />
                                </label>
                            </div>
                            <div className="modal-action">
                                <button onClick={handleDenySubmit} className="btn btn-error text-white">Submit</button>
                                <label onClick={() => setIsModalOpen(false)} htmlFor="deny-modal" className="btn text-white">Close</label>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}