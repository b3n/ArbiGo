# ArbiGo
Work in progress
<hr>

ArbiGo will be an implementation of Go that can be played on any board, the rules are:

0. The game is played on a finite graph.
1. Each player is assigned a discrete colour.
2. Play is alternate colouring of a non-coloured node.
3. A play of a colour causes loss of the other colours of all nodes without a path along the other colours to no colour and subsequently causes a loss of the colour of all nodes without a path along the colour to no colour.
4. No colouring after a play and its caused losses may be recreated.

These rules are based on graph theory. Unlike traditional rulesets they don’t specify the kind of board to play on, the number of players, the colours, or even passes and how to win. The result is a variant of go that can be played on any board, with any number of players, and play continues until there are no legal moves left (or the other players forfeits).

For more variants, see: http://en.wikipedia.org/wiki/Go_variants.
