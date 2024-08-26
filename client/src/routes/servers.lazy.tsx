import { createLazyFileRoute } from '@tanstack/react-router'
import Header from '../components/header'

export const Route = createLazyFileRoute('/servers')({
  component: ServersPage
})

function ServersPage() {
  return (
    <Header />
  )
}