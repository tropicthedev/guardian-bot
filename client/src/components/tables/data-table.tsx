import {
    createColumnHelper,
    flexRender,
    getCoreRowModel,
    useReactTable,
    getSortedRowModel,
    SortingState,
    getPaginationRowModel,
} from '@tanstack/react-table';

import mockData from '../../data.json';
import React from 'react';
import { ScrollArea } from '../scroll-area';

type Person = {
    id: number;
    name: string;
    status: string;
};

const columnHelper = createColumnHelper<Person>();

const columns = [
    columnHelper.accessor('name', {
        cell: (info) => info.getValue(),
        header: () => <span>Name</span>,

    }),
    columnHelper.accessor((row) => row.status, {
        id: 'status',
        cell: (info) => <i>{info.getValue()}</i>,
        header: () => <span>Status</span>,
    }),
    columnHelper.accessor((row) => row.status, {
        id: 'action',
        cell: () => <div className="dropdown">
            <div tabIndex={0} role="button" className="btn m-1">Click</div>
            <ul tabIndex={0} className="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow">
                <li><a className='text-lg font-semibold'>Accept</a></li>
                <li><a className='text-lg font-semibold'>Deny</a></li>
                <li><a className='text-lg font-semibold'>Ban</a></li>
                <div className="divider"></div>
                <li><a className='text-lg font-semibold'>Copy Link</a></li>

            </ul>
        </div>,
        header: () => <span>Action</span>,
        enableSorting: false
    }),
];

export default function DataTable() {
    const [data] = React.useState(() => [...mockData]);
    const [sorting, setSorting] = React.useState<SortingState>([]);

    const table = useReactTable({
        data,
        columns,
        state: {
            sorting,
        },
        initialState: {
            pagination: {
                pageSize: 10,
            },
        },
        getCoreRowModel: getCoreRowModel(),
        onSortingChange: setSorting,
        getSortedRowModel: getSortedRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
    });

    return (
        <div className="p-4 max-w-sm md:w-full md:max-w-4xl mx-auto">
            <form>
                <input
                    type="text"
                    placeholder="Type here"
                    className="input input-bordered w-full text-lg mb-4"
                />
            </form>
            <ScrollArea className='h-[80vh] max-h-[600px] w-full'>
                <table className="border-separate border-spacing-y-2 w-full text-lg">
                    <thead>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <tr key={headerGroup.id} className="border-b">
                                {headerGroup.headers.map((header) => (
                                    <th
                                        key={header.id}
                                        className="px-4 py-4 font-semibold text-left"
                                    >
                                        {header.isPlaceholder ? null : (
                                            <div
                                                {...{
                                                    className: header.column.getCanSort()
                                                        ? 'cursor-pointer select-none flex items-center'
                                                        : '',
                                                    onClick: header.column.getToggleSortingHandler(),
                                                }}
                                            >
                                                {flexRender(
                                                    header.column.columnDef.header,
                                                    header.getContext()
                                                )}
                                                {{
                                                    asc: <span className="pl-2">↑</span>,
                                                    desc: <span className="pl-2">↓</span>,
                                                }[header.column.getIsSorted() as string] ?? null}
                                            </div>
                                        )}
                                    </th>
                                ))}
                            </tr>
                        ))}
                    </thead>
                    <tbody>
                        {table.getRowModel().rows.map((row) => (
                            <tr key={row.id} className="border-b hover:bg-slate-600">
                                {row.getVisibleCells().map((cell) => (
                                    <td key={cell.id} className="px-4 pt-[14px] pb-[18px]">
                                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </ScrollArea>
            <div className="flex flex-col sm:flex-row w-full mt-8 items-center gap-2 text-lg">
                <div className="sm:mr-auto sm:mb-0 mb-2">
                    <span className="mr-2">Items per page</span>
                    <select
                        className="border p-2 rounded w-20"
                        value={table.getState().pagination.pageSize}
                        onChange={(e) => {
                            table.setPageSize(Number(e.target.value));
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
                        className="btn btn-md btn-primary text-lg text-white"
                        onClick={() => table.setPageIndex(0)}
                        disabled={!table.getCanPreviousPage()}
                    >
                        {'<<'}
                    </button>
                    <button
                        className="btn btn-md btn-primary text-lg text-white"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                    >
                        {'<'}
                    </button>
                    <span className="flex items-center gap-2">
                        <input
                            min={1}
                            max={table.getPageCount()}
                            type="number"
                            value={table.getState().pagination.pageIndex + 1}
                            onChange={(e) => {
                                const page = e.target.value ? Number(e.target.value) - 1 : 0;
                                table.setPageIndex(page);
                            }}
                            className="border p-2 rounded w-16 text-center"
                        />
                        of {table.getPageCount()}
                    </span>
                    <button
                        className="btn btn-md btn-primary text-lg text-white"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                    >
                        {'>'}
                    </button>
                    <button
                        className="btn btn-md btn-primary text-lg text-white"
                        onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                        disabled={!table.getCanNextPage()}
                    >
                        {'>>'}
                    </button>
                </div>
            </div>
        </div>
    );
}
