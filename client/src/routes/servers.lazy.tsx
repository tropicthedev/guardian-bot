// import { createLazyFileRoute } from '@tanstack/react-router'
// import { useState } from 'react';
// import { HiOutlinePlus, HiOutlineClipboardCopy, HiOutlineRefresh } from "react-icons/hi";
// import { ScrollArea } from '../components/ui/scroll-area';
// import { RiDeleteBin6Fill } from "react-icons/ri";

// export const Route = createLazyFileRoute('/servers')({
//   component: ServersPage
// })

// interface Server {
//   id: number;
//   name: string;
//   onlinePlayers: number;
//   apiKey: string;
// }

// function ServersPage() {
//   const [servers, setServers] = useState<Server[]>([
//     { id: 1, name: 'Survival', onlinePlayers: 2, apiKey: 'abcd-1234-efgh-5678' },
//     { id: 2, name: 'Creative', onlinePlayers: 4, apiKey: 'ijkl-9012-mnop-3456' }
//   ]);
//   const [selectedServer, setSelectedServer] = useState<Server | null>(null);
//   const [isServerModalOpen, setIsServerModalOpen] = useState<boolean>(false);
//   const [isAddServerModalOpen, setIsAddServerModalOpen] = useState<boolean>(false);
//   const [newServerName, setNewServerName] = useState<string>('');
//   const [generatedApiKey, setGeneratedApiKey] = useState<string>('');

//   const handleRegenerateApiKey = () => {
//     // Simulate API key regeneration
//     const newApiKey = Math.random().toString(36).substring(2, 10) + '-' + Math.random().toString(36).substring(2, 10);
//     if (selectedServer) {
//       const updatedServer = { ...selectedServer, apiKey: newApiKey };
//       setServers(servers.map(server => server.id === selectedServer.id ? updatedServer : server));
//       setSelectedServer(updatedServer);
//     }
//   };

//   const handleAddServer = () => {
//     const newServer: Server = {
//       id: servers.length + 1,
//       name: newServerName,
//       onlinePlayers: 0,
//       apiKey: Math.random().toString(36).substring(2, 10) + '-' + Math.random().toString(36).substring(2, 10)
//     };
//     setServers([...servers, newServer]);
//     setGeneratedApiKey(newServer.apiKey);
//     setIsAddServerModalOpen(false);
//   };

//   return (
//     <div className="p-4 max-w-4xl mx-auto">
//       <h1 className="text-3xl font-semibold mb-6">Server List</h1>
//       <ScrollArea className="h-[80vh] max-h-[600px] mb-4">
//         <ul className="space-y-6">
//           {servers.map((server) => (
//             <li key={server.id} className="flex items-center justify-between p-4 border rounded-lg shadow-sm">
//               <div className="flex items-center space-x-4">
//                 <img src="https://cdn.discordapp.com/attachments/781557164984238080/1062067429452300408/pfp.png?ex=66cf64df&is=66ce135f&hm=a4025337d6e7d05cc2141519b7510d8f6301bcbacae0c9497bcf54f9303309a8&" alt={`${server.name} Icon`} className="w-14 h-14 rounded-full" />
//                 <span className="text-lg font-semibold">{server.name}</span>
//               </div>
//               <div className="flex items-center space-x-5">
//                 <span className="text-lg">{server.onlinePlayers} / 20 Online</span>
//                 <button
//                   className="btn btn-secondary "
//                   onClick={() => { setSelectedServer(server); setIsServerModalOpen(true); }}
//                 >
//                   Details
//                 </button>
//               </div>
//             </li>
//           ))}
//         </ul>
//       </ScrollArea>
//       <button
//         className="btn btn-success flex items-center space-x-1 mt-4 "
//         onClick={() => setIsAddServerModalOpen(true)}
//       >
//         <HiOutlinePlus />
//         <span>Add Server</span>
//       </button>

//       {/* Server Details Modal */}
//       {isServerModalOpen && selectedServer && (
//         <>
//           <input type="checkbox" id="server-modal" className="modal-toggle" checked={isServerModalOpen} readOnly />
//           <div className="modal">
//             <div className="modal-box">
//               <h2 className="text-2xl font-semibold mb-4">Server Details</h2>
//               <p className="text-lg mb-5"><strong>Server ID:</strong> {selectedServer.id}</p>
//               <p className="text-lg mb-5"><strong>API Key:</strong> <span className="bg-gray-200 p-2 rounded text-gray-900">{selectedServer.apiKey}</span></p>
//               <button
//                 className="btn btn-secondary flex items-center space-x-1 mt-5 mb-2 "
//                 onClick={handleRegenerateApiKey}
//               >
//                 <HiOutlineRefresh />
//                 <span>Regenerate API Key</span>
//               </button>
//               <button
//                 className="btn btn-secondary flex items-center space-x-1 "
//                 onClick={() => navigator.clipboard.writeText(selectedServer.apiKey)}
//               >
//                 <HiOutlineClipboardCopy />
//                 <span>Copy API Key</span>
//               </button>
//               <button
//                 className="btn btn-error flex items-center space-x-1 mt-5 "
//                 onClick={() => navigator.clipboard.writeText(selectedServer.apiKey)}
//               >
//                 <RiDeleteBin6Fill />
//                 <span>Delete Server</span>
//               </button>
//               <div className="modal-action">
//                 <label onClick={() => setIsServerModalOpen(false)} htmlFor="server-modal" className="btn">Close</label>
//               </div>
//             </div>
//           </div>
//         </>
//       )}

//       {/* Add Server Modal */}
//       {isAddServerModalOpen && (
//         <>
//           <input type="checkbox" id="add-server-modal" className="modal-toggle" checked={isAddServerModalOpen} readOnly />
//           <div className="modal">
//             <div className="modal-box">
//               <h2 className="text-2xl font-semibold mb-4">Add Server</h2>
//               <div className="form-control">
//                 <label htmlFor="server-name" className="label">
//                   <span className="label-text">Server Name</span>
//                 </label>
//                 <input
//                   id="server-name"
//                   type="text"
//                   value={newServerName}
//                   onChange={(e) => setNewServerName(e.target.value)}
//                   className="input input-bordered w-full"
//                 />
//               </div>
//               <div className="modal-action">
//                 <label onClick={handleAddServer} htmlFor="add-server-modal" className="btn btn-success ">Save</label>
//                 <label onClick={() => setIsAddServerModalOpen(false)} htmlFor="add-server-modal" className="btn">Close</label>
//               </div>
//             </div>
//           </div>
//         </>
//       )}

//       {/* Generated API Key Modal */}
//       {generatedApiKey && (
//         <>
//           <input type="checkbox" id="api-key-modal" className="modal-toggle" checked={!!generatedApiKey} readOnly />
//           <div className="modal">
//             <div className="modal-box">
//               <h2 className="text-2xl font-semibold mb-4">API Key Generated</h2>
//               <p className="text-lg mb-2">Your new API key is:</p>
//               <p className="text-lg mb-4 bg-gray-200 p-2 rounded">{generatedApiKey}</p>
//               <button
//                 className="btn btn-secondary flex items-center space-x-1"
//                 onClick={() => { navigator.clipboard.writeText(generatedApiKey); setGeneratedApiKey(''); }}
//               >
//                 <HiOutlineClipboardCopy />
//                 <span>Copy API Key</span>
//               </button>
//               <div className="modal-action">
//                 <label onClick={() => setGeneratedApiKey('')} htmlFor="api-key-modal" className="btn">Close</label>
//               </div>
//             </div>
//           </div>
//         </>
//       )}
//     </div>
//   );
// }

import { createLazyFileRoute } from '@tanstack/react-router'
import { useState } from 'react';
import { HiChevronDoubleLeft, HiChevronDoubleRight, HiChevronLeft, HiChevronRight, HiOutlineClipboardCopy } from "react-icons/hi";
import { FaServer } from "react-icons/fa6";

export const Route = createLazyFileRoute('/servers')({
  component: ServersPage
})

function ServersPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [modalData, setModalData] = useState<{ id: number; name: string; apiToken: string } | null>(null);

  const statusMapping = {
    "ONLINE": "text-green-100 bg-green-600",
    "OFFLINE": "text-yellow-100 bg-yellow-600"
  };

  const status = ["ONLINE", "OFFLINE"];

  const minCeiled = Math.ceil(1000);
  const maxFloored = Math.floor(1000);

  const totalElements = Math.random() * (maxFloored - minCeiled + 1) + minCeiled;

  const items = Array.from({ length: totalElements }, (_, i) => ({
    id: i,
    name: 'Server ' + i,
    status: status[Math.floor(Math.random() * status.length)],
    apiToken: 'API_KEY_' + i
  }));

  const filteredItems = items.filter(item =>
    item.status.toLowerCase().includes(searchQuery.toLowerCase()) || item.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

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
        {paginatedItems.map(item => (
          <div
            className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-700 shadow-sm rounded-lg mb-2 hover:shadow-md transition-shadow duration-300"
            key={item.id}
          >
            <div className="flex items-center gap-5">
              <FaServer size={50} />
              <div>
                <h2 className="text-lg font-bold">{item.name}</h2>
                {/* @ts-expect-error No no */}
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
              <button className="btn btn-warning" onClick={() => { /* Handle regenerate key */ }}>Regenerate Key</button>
              <button className="btn btn-error" onClick={() => { /* Handle delete confirmation */ }}>Delete Server</button>
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