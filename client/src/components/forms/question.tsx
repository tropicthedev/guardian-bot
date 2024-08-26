import { useState } from 'react';
import { HiArrowSmUp, HiArrowSmDown, HiOutlineX } from "react-icons/hi";
import { ScrollArea } from '../scroll-area';

function QuestionForm() {
    const [questions, setQuestions] = useState([]);
    const [question, setQuestion] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (question.trim()) {
            setQuestions([...questions, question.trim()]);
            setQuestion('');
        }
    };

    const handleRearrange = (index, direction) => {
        const newQuestions = [...questions];
        const [removed] = newQuestions.splice(index, 1);
        newQuestions.splice(index + direction, 0, removed);
        setQuestions(newQuestions);
    };

    const handleDelete = (index) => {
        const newQuestions = questions.filter((_, i) => i !== index);
        setQuestions(newQuestions);
    };

    return (
        <div className="p-4 max-w-4xl mx-auto">
            <form onSubmit={handleSubmit} className="space-y-4">
                <div className="form-control">
                    <label htmlFor="question" className="label">
                        <span className="label-text text-3xl font-semibold">Your Question</span>
                    </label>
                    <input
                        id="question"
                        type="text"
                        value={question}
                        onChange={(e) => setQuestion(e.target.value)}
                        className="input input-bordered w-full text-lg"
                        placeholder="Type your question here..."
                    />
                </div>
                <button type="submit" className="btn btn-primary w-full text-xl text-white">Add Question</button>
            </form>
            <ScrollArea className='h-[60vh] max-h-[400px] mt-5'>
                <div className="mt-6">
                    <h2 className="text-3xl font-semibold">Submitted Questions</h2>
                    <ul className="list-disc pl-5 mt-2">
                        {questions.map((q, index) => (
                            <li key={index} className="flex items-center space-x-2 mb-10">
                                <span className='text-2xl'>{q}</span>
                                <button
                                    onClick={() => handleRearrange(index, -1)}
                                    disabled={index === 0}
                                    className="btn btn-sm btn-primary text-lg text-white"
                                >
                                    <HiArrowSmUp />
                                </button>
                                <button
                                    onClick={() => handleRearrange(index, 1)}
                                    disabled={index === questions.length - 1}
                                    className="btn btn-sm btn-secondary text-lg text-white"
                                >
                                    <HiArrowSmDown />
                                </button>
                                <button
                                    onClick={() => handleDelete(index)}
                                    disabled={false}
                                    className="btn btn-sm btn-error text-lg text-white"
                                >
                                    <HiOutlineX />
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </ScrollArea>
            <form>
                <button type="submit" className="btn btn-success w-full text-xl text-white">Save</button>
            </form>
        </div>
    );
}

export default QuestionForm;
