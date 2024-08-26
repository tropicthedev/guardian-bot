import { ColumnDef } from "@tanstack/react-table"

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type Application = {
    id: string
    status: "PENDING" | "ACCEPTED" | "DENIED" | "BANNED"
    name: string
}

export const columns: ColumnDef<Application>[] = [
    {
        accessorKey: "status",
        header: "Status",
    },
    {
        accessorKey: "name",
        header: "Name",
    }
]