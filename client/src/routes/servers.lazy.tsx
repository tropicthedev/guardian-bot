import { createLazyFileRoute } from '@tanstack/react-router'

export const Route = createLazyFileRoute('/servers')({
  component: ServersPage
})

function ServersPage() {
  return (
    <>
    </>
  )
}