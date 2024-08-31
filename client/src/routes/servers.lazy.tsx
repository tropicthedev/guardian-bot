import { createLazyFileRoute } from '@tanstack/react-router';
import { useState } from 'react';
import { HiChevronDoubleLeft, HiChevronDoubleRight, HiChevronLeft, HiChevronRight, HiOutlineClipboardCopy } from "react-icons/hi";
import { FaServer } from "react-icons/fa6";
import { useQuery, useMutation } from '@tanstack/react-query';
import { ServerListSchema } from '../lib/types';
import { z } from 'zod';


const fetchServers = async (): Promise<z.infer<typeof ServerListSchema>> => {
  const response = await fetch('http://localhost:1234/api/servers');
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json();
  return ServerListSchema.parse(data);
};

const regenerateApiKey = async (serverId: number) => {
  const response = await fetch(`/api/servers/${serverId}/regenerate-key`, {
    method: "POST",
  });
  return response.json();
};

const deleteServer = async (serverId: number) => {
  const response = await fetch(`/api/servers/${serverId}`, { method: "DELETE", });
  return response.json();
};

export const Route = createLazyFileRoute('/servers')({
  component: ServersPage
});

function ServersPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [modalData, setModalData] = useState<{ id: number; name: string; apiToken: string } | null>(null);

  // Fetching server data
  const { data: serverList, isLoading, error } = useQuery({
    queryKey: ['servers'],
    queryFn: fetchServers
  });

  // Mutation hooks
  const { mutate: mutateRegenerateApiKey, isPending: isRegeneratingKey } = useMutation({
    mutationFn: regenerateApiKey
  });

  const { mutate: mutateDeleteServer, isPending: isDeletingServer } = useMutation({
    mutationFn: deleteServer
  });

  const statusMapping = {
    "ONLINE": "text-green-100 bg-green-600",
    "OFFLINE": "text-yellow-100 bg-yellow-600"
  };

  const filteredItems = serverList?.filter(item =>
    item.status.toLowerCase().includes(searchQuery.toLowerCase()) || item.name.toLowerCase().includes(searchQuery.toLowerCase())
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

  const openModal = (id: number, name: string, apiToken: string) => {
    setModalData({ id, name, apiToken });
  };

  const closeModal = () => {
    setModalData(null);
  };

  return (
    <>
      <div className="flex justify-center items-center p-4">
        <form className="w-full max-w-xs">
          <input
            type="text"
            placeholder="Search Servers"
            className="input input-bordered input-primary w-full rounded-full focus:ring-2 focus:ring-primary focus:outline-none"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>
      </div>
      <div className="pl-5 pr-5 w-full max-w-4xl mx-auto">
        {isLoading && <p>Loading...</p>}
        {error && <p>An error occurred: {error.message}</p>}
        {paginatedItems?.map(item => (
          <div
            className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-700 shadow-sm rounded-lg mb-2 hover:shadow-md transition-shadow duration-300"
            key={item.id}
          >
            <div className="flex items-center gap-5">
              <FaServer size={50} />
              <div>
                <h2 className="text-lg font-bold">{item.name}</h2>
                {/* @ts-expect-error ignore whatever this is bitching about */}
                <p className={`text-sm font-medium py-1 px-2 rounded-lg w-fit ${statusMapping[item.status]} font-semibold`}>
                  {item.status}
                </p>
                <p className="text-md text-gray-100">Players Online: 1 / 20 </p>
              </div>
            </div>
            <div className="flex gap-2 mt-2 sm:mt-0">
              <button
                className="btn btn-md btn-secondary"
                onClick={() => openModal(item.id, item.name, item.apiToken)}
              >
                Manage Server
              </button>
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
      </div>

      {/* Modal for managing server */}
      {modalData && (
        <div className="modal modal-open">
          <div className="modal-box">
            <h3 className="font-bold text-lg">{modalData.name}</h3>
            <p className="py-4">Server ID: {modalData.id}</p>
            <div className="flex items-center gap-2">
              <span>API Token: {modalData.apiToken}</span>
              <button className="btn btn-sm btn-outline" onClick={() => navigator.clipboard.writeText(modalData.apiToken)}>
                <HiOutlineClipboardCopy className="text-lg" />
              </button>
            </div>
            <div className="modal-action">
              <button
                className="btn btn-warning"
                onClick={() => mutateRegenerateApiKey(modalData.id)}
                disabled={isRegeneratingKey}
              >
                {isRegeneratingKey ? 'Regenerating...' : 'Regenerate Key'}
              </button>
              <button
                className="btn btn-error"
                onClick={() => mutateDeleteServer(modalData.id)}
                disabled={isDeletingServer}
              >
                {isDeletingServer ? 'Deleting...' : 'Delete Server'}
              </button>
              <button className="btn" onClick={closeModal}>Close</button>
            </div>
          </div>
        </div>
      )}

      {/* Add Server Button */}
      <div className="fixed bottom-4 right-4">
        <button className="btn btn-secondary" onClick={() => { /* Handle opening add server modal */ }}>
          Add Server
        </button>
      </div>
    </>
  );
}