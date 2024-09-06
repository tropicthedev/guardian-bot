import { createLazyFileRoute } from '@tanstack/react-router';
import { useState, useEffect } from 'react';
import { HiChevronDoubleLeft, HiChevronDoubleRight, HiChevronLeft, HiChevronRight, HiOutlineClipboardCopy } from "react-icons/hi";
import { FaServer } from "react-icons/fa6";
import { useQuery, useMutation } from '@tanstack/react-query';
import { ServerListSchema } from '../lib/types';
import { z } from 'zod';

const fetchServers = async (): Promise<z.infer<typeof ServerListSchema>> => {
  const response = await fetch('/api/servers');
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

  const { data: serverList, isLoading, error } = useQuery({
    queryKey: ['servers'],
    queryFn: fetchServers
  });

  const { mutate: mutateRegenerateApiKey, isPending: isRegeneratingKey } = useMutation({
    mutationFn: regenerateApiKey
  });

  const { mutate: mutateDeleteServer, isPending: isDeletingServer } = useMutation({
    mutationFn: deleteServer
  });

  const [filteredItems, setFilteredItems] = useState<z.infer<typeof ServerListSchema> | null>(null);

  useEffect(() => {
    if (serverList) {
      const filtered = serverList.filter(item =>
        item.status.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.name.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredItems(filtered);
      setCurrentPage(1);
    }
  }, [searchQuery, serverList]);

  const actualFilteredItems = filteredItems || [];
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedItems = actualFilteredItems.slice(startIndex, startIndex + itemsPerPage);
  const totalPages = Math.ceil(actualFilteredItems.length / itemsPerPage);

  const handleFirstPage = () => setCurrentPage(1);
  const handlePreviousPage = () => setCurrentPage(prev => Math.max(prev - 1, 1));
  const handleNextPage = () => setCurrentPage(prev => Math.min(prev + 1, totalPages));
  const handleLastPage = () => setCurrentPage(totalPages);

  const openModal = (id: number, name: string, apiToken: string) => {
    setModalData({ id, name, apiToken });
  };

  const closeModal = () => {
    setModalData(null);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <input
          type="text"
          placeholder="Search Servers"
          className="input input-bordered w-full max-w-xs"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center">
          <span className="loading loading-spinner loading-lg"></span>
        </div>
      ) : error ? (
        <div className="alert alert-error">
          <svg xmlns="http://www.w3.org/2000/svg" className="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span>Error: {error instanceof Error ? error.message : 'An error occurred'}</span>
        </div>
      ) : (
        <>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {paginatedItems.map(item => (
              <div className="card bg-base-200 shadow-xl" key={item.id}>
                <div className="card-body">
                  <div className="flex items-center mb-4">
                    <FaServer size={40} className="mr-4" />
                    <div>
                      <h2 className="card-title">{item.name}</h2>
                      <span className={`badge ${item.status === 'ONLINE' ? 'badge-success' : 'badge-warning'}`}>
                        {item.status}
                      </span>
                    </div>
                  </div>
                  <p>Players Online: 1 / 20</p>
                  <div className="card-actions justify-end mt-4">
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => openModal(item.id, item.name, item.apiToken)}
                    >
                      Manage Server
                    </button>
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

      <div className="fixed bottom-4 right-4">
        <button className="btn btn-primary" onClick={() => { /* Handle opening add server modal */ }}>
          Add Server
        </button>
      </div>
    </div>
  );
}