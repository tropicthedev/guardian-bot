import { useEffect, useState } from 'react';
import Header from '../../components/header';
import { DataTable } from '../../components/tables/data-table';
import { Application, columns } from '../../components/tables/columns';
import { faker } from '@faker-js/faker';
import { createFileRoute } from '@tanstack/react-router';

async function getData(): Promise<Application[]> {
  const minCeiled = Math.ceil(10);
  const maxFloored = Math.floor(15);

  const totalElements = Math.random() * (maxFloored - minCeiled + 1) + minCeiled;
  const items: Application[] = [];

  for (let i = 0; i < totalElements; i++) {
    items.push({
      id: crypto.randomUUID(),
      status: "PENDING",
      name: faker.person.firstName()
    })
  }
  return items;
}

function ApplicationsPage() {
  const [data, setData] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const fetchedData = await getData();
        setData(fetchedData);
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Header />
      <div className='p-5 w-full justify-center'>
        <DataTable columns={columns} data={data} />
      </div>
    </>
  );
}


export const Route = createFileRoute('/applications/')({
  component: ApplicationsPage
})
