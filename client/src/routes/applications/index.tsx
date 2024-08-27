import { createFileRoute } from '@tanstack/react-router';
import DataTable from '../../components/tables/data-table';
import QuestionForm from '../../components/forms/question';

export const Route = createFileRoute('/applications/')({
  component: ApplicationsPage
})

function ApplicationsPage() {

  return (
    <>

      <div className='flex flex-col justify-center items-center w-full overflow-hidden'>
        {/* @ts-expect-error This is a document error its fine */}
        <button className="btn btn-primary p-4 max-w-sm md:w-full md:max-w-4xl mx-auto text-white" onClick={() => document?.getElementById('my_modal_1').showModal()}>Application Questions</button>
        <dialog id="my_modal_1" className="modal">
          <div className="modal-box">
            <QuestionForm />
            <div className="modal-action">
              <form method="dialog">
                {/* if there is a button in form, it will close the modal */}
                <button className="btn">Close</button>
              </form>
            </div>
          </div>
        </dialog>
        <DataTable />
      </div>
    </>
  );
}



