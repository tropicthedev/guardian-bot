import { useState, useEffect } from 'react';
import { createLazyFileRoute } from '@tanstack/react-router';
import { HiChevronDoubleLeft, HiChevronLeft, HiChevronRight, HiChevronDoubleRight } from 'react-icons/hi';
import { faker } from '@faker-js/faker';

export const Route = createLazyFileRoute('/players')({
  component: PlayersPage
});

function PlayersPage() {
  const statusMapping = {
    "ACTIVE": "text-green-100 bg-green-600",
    "INACTIVE": "text-gray-100 bg-gray-600",
    "NEW": "text-blue-100 bg-blue-600",
    "VACATION": "text-yellow-100 bg-yellow-600"
  };

  const status = ["ACTIVE", "INACTIVE", "NEW", "VACATION"];

  const minCeiled = Math.ceil(1000);
  const maxFloored = Math.floor(1000);

  const totalElements = Math.random() * (maxFloored - minCeiled + 1) + minCeiled;

  const items = Array.from({ length: totalElements }, (_, i) => ({
    id: i,
    name: faker.person.firstName(),
    status: status[Math.floor(Math.random() * status.length)],
    joinDate: faker.date.recent().toDateString(),
    purgeDate: faker.date.future().toDateString(),
    avatar: faker.image.avatarGitHub()
    // `https://api.mineatar.io/face/069a79f444e94726a5befca90e38aaf5`
  }));

  const [searchQuery, setSearchQuery] = useState('');
  const [filteredItems, setFilteredItems] = useState(items);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  useEffect(() => {
    const filtered = items.filter(item =>
      item.status.toLowerCase().includes(searchQuery.toLowerCase()) || item.name.toLowerCase().includes(searchQuery.toLowerCase())
      || item.joinDate.toLowerCase().includes(searchQuery.toLowerCase()) || item.purgeDate.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredItems(filtered);
    setCurrentPage(1);
  }, [searchQuery]);

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
            placeholder="Search Members"
            className="input input-bordered input-primary w-full rounded-full focus:ring-2 focus:ring-primary focus:outline-none"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>
      </div>
      <div className="pl-5 pr-5 w-full max-w-4xl mx-auto">
        {paginatedItems.map(item => (
          <div
            className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-700 shadow-sm rounded-lg mb-2 hover:shadow-md transition-shadow duration-300"
            key={item.id}
          >
            <div className="flex items-center">
              <img
                src={item.avatar}
                className="w-12 h-12 rounded-full mr-5"
                alt="Player Avatar"
              />
              <div>
                <h2 className="text-lg font-bold">{item.name}</h2>
                <p
                  // @ts-expect-error No no
                  className={`text-md font-medium py-2 px-2 rounded-lg w-fit ${statusMapping[item.status]} font-semibold`}
                >
                  {item.status}
                </p>
                <p className="text-md text-gray-100">Join Date: {item.joinDate}</p>
                <p className="text-md text-gray-100">Purge Date: {item.purgeDate}</p>
              </div>
            </div>
            <div className="flex gap-2 mt-2 sm:mt-0">
              <button className="btn btn-md btn-secondary">Vacation</button>
              <button className="btn btn-md btn-warning">Kick</button>
              <button className="btn btn-md btn-error">Ban</button>
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
                setCurrentPage(1); // Reset to the first page when items per page changes
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
      </div>
    </>
  )
}
