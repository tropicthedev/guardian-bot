import { createLazyFileRoute } from '@tanstack/react-router'
import { useState } from 'react';
import { HiOutlinePlus, HiOutlineClipboardCopy, HiOutlineRefresh } from "react-icons/hi";
import { ScrollArea } from '../components/ui/scroll-area';
import { RiDeleteBin6Fill } from "react-icons/ri";

export const Route = createLazyFileRoute('/servers')({
  component: ServersPage
})

interface Server {
  id: number;
  name: string;
  onlinePlayers: number;
  apiKey: string;
}

function ServersPage() {
  const [servers, setServers] = useState<Server[]>([
    { id: 1, name: 'Survival', onlinePlayers: 2, apiKey: 'abcd-1234-efgh-5678' },
    { id: 2, name: 'Creative', onlinePlayers: 4, apiKey: 'ijkl-9012-mnop-3456' }
  ]);
  const [selectedServer, setSelectedServer] = useState<Server | null>(null);
  const [isServerModalOpen, setIsServerModalOpen] = useState<boolean>(false);
  const [isAddServerModalOpen, setIsAddServerModalOpen] = useState<boolean>(false);
  const [newServerName, setNewServerName] = useState<string>('');
  const [generatedApiKey, setGeneratedApiKey] = useState<string>('');

  const handleRegenerateApiKey = () => {
    // Simulate API key regeneration
    const newApiKey = Math.random().toString(36).substring(2, 10) + '-' + Math.random().toString(36).substring(2, 10);
    if (selectedServer) {
      const updatedServer = { ...selectedServer, apiKey: newApiKey };
      setServers(servers.map(server => server.id === selectedServer.id ? updatedServer : server));
      setSelectedServer(updatedServer);
    }
  };

  const handleAddServer = () => {
    const newServer: Server = {
      id: servers.length + 1,
      name: newServerName,
      onlinePlayers: 0,
      apiKey: Math.random().toString(36).substring(2, 10) + '-' + Math.random().toString(36).substring(2, 10)
    };
    setServers([...servers, newServer]);
    setGeneratedApiKey(newServer.apiKey);
    setIsAddServerModalOpen(false);
  };

  return (
    <div className="p-4 max-w-4xl mx-auto">
      <h1 className="text-3xl font-semibold mb-6">Server List</h1>
      <ScrollArea className="h-[80vh] max-h-[600px] mb-4">
        <ul className="space-y-6">
          {servers.map((server) => (
            <li key={server.id} className="flex items-center justify-between p-4 border rounded-lg shadow-sm">
              <div className="flex items-center space-x-4">
                <img src="https://cdn.discordapp.com/attachments/781557164984238080/1062067429452300408/pfp.png?ex=66cf64df&is=66ce135f&hm=a4025337d6e7d05cc2141519b7510d8f6301bcbacae0c9497bcf54f9303309a8&" alt={`${server.name} Icon`} className="w-14 h-14 rounded-full" />
                <span className="text-lg font-semibold">{server.name}</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="text-lg">{server.onlinePlayers} / 20 Online</span>
                <button
                  className="btn btn-primary text-white"
                  onClick={() => { setSelectedServer(server); setIsServerModalOpen(true); }}
                >
                  Details
                </button>
              </div>
            </li>
          ))}
        </ul>
      </ScrollArea>
      <button
        className="btn btn-success flex items-center space-x-1 mt-4 text-white"
        onClick={() => setIsAddServerModalOpen(true)}
      >
        <HiOutlinePlus />
        <span>Add Server</span>
      </button>

      {/* Server Details Modal */}
      {isServerModalOpen && selectedServer && (
        <>
          <input type="checkbox" id="server-modal" className="modal-toggle" checked={isServerModalOpen} readOnly />
          <div className="modal">
            <div className="modal-box">
              <h2 className="text-2xl font-semibold mb-4">Server Details</h2>
              <p className="text-lg mb-5"><strong>Server ID:</strong> {selectedServer.id}</p>
              <p className="text-lg mb-5"><strong>API Key:</strong> <span className="bg-gray-200 p-2 rounded text-gray-900">{selectedServer.apiKey}</span></p>
              <button
                className="btn btn-secondary flex items-center space-x-1 mt-5 mb-2 text-white"
                onClick={handleRegenerateApiKey}
              >
                <HiOutlineRefresh />
                <span>Regenerate API Key</span>
              </button>
              <button
                className="btn btn-primary flex items-center space-x-1 text-white"
                onClick={() => navigator.clipboard.writeText(selectedServer.apiKey)}
              >
                <HiOutlineClipboardCopy />
                <span>Copy API Key</span>
              </button>
              <button
                className="btn btn-error flex items-center space-x-1 mt-5 text-white"
                onClick={() => navigator.clipboard.writeText(selectedServer.apiKey)}
              >
                <RiDeleteBin6Fill />
                <span>Delete Server</span>
              </button>
              <div className="modal-action">
                <label onClick={() => setIsServerModalOpen(false)} htmlFor="server-modal" className="btn">Close</label>
              </div>
            </div>
          </div>
        </>
      )}

      {/* Add Server Modal */}
      {isAddServerModalOpen && (
        <>
          <input type="checkbox" id="add-server-modal" className="modal-toggle" checked={isAddServerModalOpen} readOnly />
          <div className="modal">
            <div className="modal-box">
              <h2 className="text-2xl font-semibold mb-4">Add Server</h2>
              <div className="form-control">
                <label htmlFor="server-name" className="label">
                  <span className="label-text">Server Name</span>
                </label>
                <input
                  id="server-name"
                  type="text"
                  value={newServerName}
                  onChange={(e) => setNewServerName(e.target.value)}
                  className="input input-bordered w-full"
                />
              </div>
              <div className="modal-action">
                <label onClick={handleAddServer} htmlFor="add-server-modal" className="btn btn-success text-white">Save</label>
                <label onClick={() => setIsAddServerModalOpen(false)} htmlFor="add-server-modal" className="btn">Close</label>
              </div>
            </div>
          </div>
        </>
      )}

      {/* Generated API Key Modal */}
      {generatedApiKey && (
        <>
          <input type="checkbox" id="api-key-modal" className="modal-toggle" checked={!!generatedApiKey} readOnly />
          <div className="modal">
            <div className="modal-box">
              <h2 className="text-2xl font-semibold mb-4">API Key Generated</h2>
              <p className="text-lg mb-2">Your new API key is:</p>
              <p className="text-lg mb-4 bg-gray-200 p-2 rounded">{generatedApiKey}</p>
              <button
                className="btn btn-primary flex items-center space-x-1"
                onClick={() => { navigator.clipboard.writeText(generatedApiKey); setGeneratedApiKey(''); }}
              >
                <HiOutlineClipboardCopy />
                <span>Copy API Key</span>
              </button>
              <div className="modal-action">
                <label onClick={() => setGeneratedApiKey('')} htmlFor="api-key-modal" className="btn">Close</label>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
