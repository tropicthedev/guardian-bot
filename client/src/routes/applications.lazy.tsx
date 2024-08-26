import { createLazyFileRoute } from '@tanstack/react-router'
import Header from '../components/header'

export const Route = createLazyFileRoute('/applications')({
  component: ApplicationsPage
})

function ApplicationsPage() {
  return (
    <Header />
  )
}