import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/about')({
    component: Index,
})

function Index() {
    return (
        <div className="p-2">
            <h3>Welcome About!</h3>
        </div>
    )
}
