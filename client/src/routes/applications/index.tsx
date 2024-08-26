import Header from '../../components/header';
import { createFileRoute } from '@tanstack/react-router';
import DataTable from '../../components/tables/data-table';

export const Route = createFileRoute('/applications/')({
  component: ApplicationsPage
})

function ApplicationsPage() {

  return (
    <>
      <Header />
      <div className='flex flex-col justify-center items-center w-full overflow-hidden'>
        <DataTable />
      </div>
    </>
  );
}



