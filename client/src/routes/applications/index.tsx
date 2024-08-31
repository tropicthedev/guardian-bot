import { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import { HiChevronDoubleLeft, HiChevronDoubleRight, HiChevronLeft, HiChevronRight } from "react-icons/hi";
import { Link } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import { ApplicationListSchema } from '../../lib/types';
import { z } from 'zod';
import QuestionForm from '../../components/forms/question';

// Fetch function
const fetchApplications = async (): Promise<z.infer<typeof ApplicationListSchema>> => {
  const response = await fetch('http://localhost:1234/api/applications');
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json();
  return ApplicationListSchema.parse(data);
};

export const Route = createFileRoute('/applications/')({
  component: ApplicationsPage
});

function ApplicationsPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);

  const { data: applications, isLoading, error } = useQuery({
    queryKey: ['applications'],
    queryFn: fetchApplications
  });

  const statusMapping = {
    "ACCEPTED": "text-green-100 bg-green-600",
    "DENIED": "text-yellow-100 bg-yellow-600",
    "PENDING": "text-blue-100 bg-blue-600",
    "BANNED": "text-red-100 bg-red-600"
  };

  const filteredItems = applications?.filter(item =>
    item.status.toLowerCase().includes(searchQuery.toLowerCase()) ||
    item.name.toLowerCase().includes(searchQuery.toLowerCase())
  ) ?? [];

  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedItems = filteredItems.slice(startIndex, startIndex + itemsPerPage);
  const totalPages = Math.ceil(filteredItems.length / itemsPerPage);

  const handleFirstPage = () => setCurrentPage(1);
  const handlePreviousPage = () => setCurrentPage(prev => Math.max(prev - 1, 1));
  const handleNextPage = () => setCurrentPage(prev => Math.min(prev + 1, totalPages));
  const handleLastPage = () => setCurrentPage(totalPages);

  const handlePageInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const page = e.target.value ? Number(e.target.value) : 1;
    setCurrentPage(Math.min(Math.max(page, 1), totalPages));
  };

  return (
    <>
      <div className="flex justify-center items-center p-4">
        <form className="w-full max-w-xs">
          <input
            type="text"
            placeholder="Search Applications"
            className="input input-bordered input-primary w-full rounded-full focus:ring-2 focus:ring-primary focus:outline-none"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>
      </div>
      <div className="pl-5 pr-5 w-full max-w-4xl mx-auto">
        {isLoading ? (
          <div className="flex justify-center items-center p-4">
            <p>Loading...</p>
          </div>
        ) : error ? (
          <div className="flex justify-center items-center p-4">
            <p>An error occurred: {error.message}</p>
          </div>
        ) : (
          <>
            {paginatedItems.map(item => (
              <div
                className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-700 shadow-sm rounded-lg mb-2 hover:shadow-md transition-shadow duration-300"
                key={item.id}
              >
                <div className="flex items-center gap-5">
                  <img src={item.avatar} alt={item.name} className="w-12 h-12 rounded-full" />
                  <div>
                    <h2 className="text-lg font-bold">{item.name}</h2>
                    {/* @ts-expect-error This is some type bs that i dont care about */}
                    <p className={`text-sm font-medium py-1 px-2 rounded-lg w-fit ${statusMapping[item.status]} font-semibold`}>
                      {item.status}
                    </p>
                    {item.status === "DENIED" && <p className="text-md text-gray-100">Denied by: Tropic</p>}
                    {item.status === "DENIED" && <p className="text-md text-gray-100">Reason: No</p>}
                    {item.status === "BANNED" && <p className="text-md text-gray-100">Banned by: Tropic</p>}
                    {item.status === "BANNED" && <p className="text-md text-gray-100">Reason: No</p>}
                  </div>
                </div>
                <div className="dropdown ml-2">
                  <div tabIndex={0} role="button" className="btn btn-secondary m-1">More</div>
                  <ul tabIndex={0} className="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow">
                    <li><a className='text-lg font-semibold'>Accept</a></li>
                    <li><a className='text-lg font-semibold'>Deny</a></li>
                    <li><a className='text-lg font-semibold'>Ban</a></li>
                    <div className="divider"></div>
                    <li>
                      <Link
                        to={`/applications/${item.id}`}
                        className='text-lg font-semibold'
                      >
                        Details
                      </Link>
                    </li>
                    <li><a className='text-lg font-semibold'>Copy Link</a></li>
                  </ul>
                </div>
              </div>
            ))}
            <div className="flex flex-col sm:flex-row w-full mt-8 items-center gap-2 text-lg">
              <div className="sm:mr-auto sm:mb-0 mb-2">
                <span className="mr-2">Items per page</span>
                <select
                  className="p-2 rounded w-20 select select-bordered select-lg"
                  value={itemsPerPage}
                  onChange={(e) => {
                    setItemsPerPage(Number(e.target.value));
                    setCurrentPage(1);
                  }}
                >
                  {[10, 25, 50, 100].map((pageSize) => (
                    <option key={pageSize} value={pageSize}>
                      {pageSize}
                    </option>
                  ))}
                </select>
              </div>
              <div className="flex gap-2">
                <button
                  className="btn btn-md btn-secondary text-lg"
                  onClick={handleFirstPage}
                  disabled={currentPage === 1}
                >
                  <HiChevronDoubleLeft />
                </button>
                <button
                  className="btn btn-md btn-secondary text-lg"
                  onClick={handlePreviousPage}
                  disabled={currentPage === 1}
                >
                  <HiChevronLeft />
                </button>
                <span className="flex items-center gap-2">
                  <input
                    min={1}
                    max={totalPages}
                    type="number"
                    value={currentPage}
                    onChange={handlePageInputChange}
                    className="input input-bordered p-2 rounded w-16 text-center"
                  />
                  of {totalPages}
                </span>
                <button
                  className="btn btn-md btn-secondary text-lg"
                  onClick={handleNextPage}
                  disabled={currentPage === totalPages}
                >
                  <HiChevronRight />
                </button>
                <button
                  className="btn btn-md btn-secondary text-lg"
                  onClick={handleLastPage}
                  disabled={currentPage === totalPages}
                >
                  <HiChevronDoubleRight />
                </button>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Add Application Button */}
      <button className="btn btn-secondary fixed bottom-4 right-4" onClick={() => setModalOpen(true)}>
        Application Questions
      </button>
      {modalOpen && (
        <dialog open className="modal">
          <div className="modal-box">
            <QuestionForm />
            <div className="modal-action">
              <button className="btn" onClick={() => setModalOpen(false)}>Close</button>
            </div>
          </div>
        </dialog>
      )}
    </>
  );
}
