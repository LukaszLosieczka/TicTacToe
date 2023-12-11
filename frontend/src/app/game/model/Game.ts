export interface Game{
  id: string
  player1: string
  player1Sign: string
  player2: string
  player2Sign: string
  board: string [][]
  nextPlayer: string | null
  creationDate: Date
  isFinished: boolean
  winner: string | null
}
