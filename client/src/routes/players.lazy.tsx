import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/players')({
  component: PlayersPage
})

function ExampleCards({ uuid }: { uuid: string }) {
  const statusMapping = {
    "ACTIVE": "badge-success",
    "INACTIVE": "badge-secondary",
    "NEW": "badge-primary",
    "VACATION": "badge-accent"
  };

  const status = ["ACTIVE", "INACTIVE", "NEW", "VACATION"];

  const minCeiled = Math.ceil(10);
  const maxFloored = Math.floor(15);

  const totalElements = Math.random() * (maxFloored - minCeiled + 1) + minCeiled;

  const items = [];

  for (let i = 0; i < totalElements; i++) {
    const currentStatus = status[Math.floor(Math.random() * status.length)];
    items.push(
      <div className="card card-compact card-bordered bg-base-100 w-80 shadow-xl" key={i}>
        <figure>
          <img
            src={`https://api.mineatar.io/body/full/${uuid}`}
            className='w-auto'
            alt="Shoes" />
        </figure>
        <div className="card-body">
          <h2 className="card-title">
            Notch
            {/* @ts-expect-error Badge Colors */}
            <div className={`badge ${statusMapping[currentStatus]}`}>
              {currentStatus}
            </div>
          </h2>
          <p className='font-bold'>Join Date: December 1, 2024</p>
          <p className='font-bold text-red-300'>Purge Date: December 31, 2024</p>
          <div className="card-actions justify-end">
            <button className='btn btn-secondary'>Vacation</button>
            <button className="btn btn-warning">Kick</button>
            <button className="btn btn-error">Ban</button>
          </div>
        </div>
      </div>
    );
  }

  return <>{items}</>
}


function PlayersPage() {
  return (
    <>
      <div className=' flex flex-wrap w-full justify-center'>
        <form>
          <input
            type="text"
            placeholder="Search Members"
            className="input input-bordered input-primary w-full max-w-xs " />
          <div className="join mt-5">
            <button className="join-item btn">«</button>
            <button className="join-item btn">Page 22</button>
            <button className="join-item btn">»</button>
          </div>
        </form >
      </div >
      <div className="flex flex-wrap gap-4 p-10  justify-center" >
        <ExampleCards uuid='069a79f444e94726a5befca90e38aaf5' />
      </div>
    </>
  )
}