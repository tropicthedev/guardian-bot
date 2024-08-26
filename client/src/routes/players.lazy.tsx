import { createLazyFileRoute } from '@tanstack/react-router'
import Header from '../components/header'

export const Route = createLazyFileRoute('/players')({
  component: PlayersPage
})

function PlayersPage() {
  return (
    <Header />
  )
}