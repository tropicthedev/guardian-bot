import { useState, useEffect } from 'react';
import { createLazyFileRoute } from '@tanstack/react-router';
import { HiChevronDoubleLeft, HiChevronLeft, HiChevronRight, HiChevronDoubleRight } from 'react-icons/hi';
import { useQuery } from '@tanstack/react-query';
import { Player, PlayerListSchema } from '../lib/types';

export const Route = createLazyFileRoute('/players')({
  component: PlayersPage
}); 

function PlayersPage() {
  const statusMapping = {
    "ACTIVE": "badge-success",
    "INACTIVE": "badge-ghost",
    "NEW": "badge-info",
    "VACATION": "badge-warning"
  };

  const { isPending, error, data } = useQuery({
    queryKey: ['playerData'],
    queryFn: async () => {
      const response = await fetch('http://localhost:1234/api/players');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return await response.json();
    },
  });

  const [playerList, setPlayerList] = useState<Player[] | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredItems, setFilteredItems] = useState<Player[] | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  useEffect(() => {
    if (data) {
      const parsedData = PlayerListSchema.parse(data);
      setPlayerList(parsedData);
    }
  }, [data]);

  useEffect(() => {
    if (playerList) {
      const filtered = playerList.filter(item =>
        item.status.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.joinDate.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.purgeDate.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredItems(filtered);
      setCurrentPage(1);
    }
  }, [searchQuery, playerList]);

  const actualFilteredItems = filteredItems || [];
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedItems = actualFilteredItems.slice(startIndex, startIndex + itemsPerPage);
  const totalPages = Math.ceil(actualFilteredItems.length / itemsPerPage);

  const handleFirstPage = () => setCurrentPage(1);
  const handlePreviousPage = () => setCurrentPage(prev => Math.max(prev - 1, 1));
  const handleNextPage = () => setCurrentPage(prev => Math.min(prev + 1, totalPages));
  const handleLastPage = () => setCurrentPage(totalPages);

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <input
          type="text"
          placeholder="Search Members"
          className="input input-bordered w-full max-w-xs"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {isPending ? (
        <div className="flex justify-center items-center">
          <span className="loading loading-spinner loading-lg"></span>
        </div>
      ) : error ? (
        <div className="alert alert-error">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span>Error: {error.message}</span>
        </div>
      ) : (
        <>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {paginatedItems.map(item => (
              <div className="card bg-base-200 shadow-xl" key={item.id}>
                <div className="card-body">
                  <div className="flex items-center mb-4">
                    <div className="avatar mr-4">
                      <div className="w-16 rounded-full">
                        <img src={item.avatar} alt="Player Avatar" />
                      </div>
                    </div>
                    <div>
                      <h2 className="card-title">{item.name}</h2>
                      <span className={`badge ${statusMapping[item.status as keyof typeof statusMapping]}`}>
                        {item.status}
                      </span>
                    </div>
                  </div>
                  <p>Join Date: {item.joinDate}</p>
                  <p>Purge Date: {item.purgeDate}</p>
                  <div className="card-actions justify-end mt-4">
                    <button className="btn btn-primary btn-sm">Vacation</button>
                    <button className="btn btn-warning btn-sm">Kick</button>
                    <button className="btn btn-error btn-sm">Ban</button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="flex flex-col sm:flex-row justify-between items-center mt-8">
            <div className="mb-4 sm:mb-0">
              <span className="mr-2">Items per page</span>
              <select
                className="select select-bordered"
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
            <div className="btn-group">
              <button
                className="btn btn-sm"
                onClick={handleFirstPage}
                disabled={currentPage === 1}
              >
                <HiChevronDoubleLeft />
              </button>
              <button
                className="btn btn-sm"
                onClick={handlePreviousPage}
                disabled={currentPage === 1}
              >
                <HiChevronLeft />
              </button>
              <button className="btn btn-sm">
                Page {currentPage} of {totalPages}
              </button>
              <button
                className="btn btn-sm"
                onClick={handleNextPage}
                disabled={currentPage === totalPages}
              >
                <HiChevronRight />
              </button>
              <button
                className="btn btn-sm"
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
  );
}