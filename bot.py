import numpy as np
import random

ROWS = 6
COLS = 8
EMPTY = 0
PLAYER_X = 1
PLAYER_O = 2

def create_board():
    return np.zeros((ROWS, COLS), dtype=int)

def is_valid_move(board, col):
    return board[0][col] == EMPTY

def drop_piece(board, col, player):
    for row in range(ROWS-1, -1, -1):
        if board[row][col] == EMPTY:
            board[row][col] = player
            return

def get_next_states(board, player):
    states = []
    for col in range(COLS):
        if is_valid_move(board, col):
            next_board = board.copy()
            drop_piece(next_board, col, player)
            states.append(next_board)
    return states


def evaluate_closing(board, player):
    opponent = PLAYER_X if player == PLAYER_O else PLAYER_O
    valid_locations = get_valid_locations(board)

    # Heuristika 1: Pobednički potezi
    for col in valid_locations:
        row = get_next_open_row(board, col)
        temp_board = board.copy()
        drop_piece(temp_board, col, player)
        if winning_move(temp_board, player):
            return 1000  # Prioritet na pobedničkom potezu

    # Heuristika 2: Blokiranje protivnika
    for col in valid_locations:
        row = get_next_open_row(board, col)
        temp_board = board.copy()
        drop_piece(temp_board, col, opponent)
        if winning_move(temp_board, opponent):
            return 900  # Prioritet na blokiranju protivnika

    # Heuristika 3: Napadi sa više opcija
    max_player_options = 0
    for col in valid_locations:
        row = get_next_open_row(board, col)
        temp_board = board.copy()
        drop_piece(temp_board, col, player)
        player_options = len(get_valid_locations(temp_board))
        if player_options > max_player_options:
            max_player_options = player_options

    # Heuristika 4: Blokiranje protivnikovih opcija
    max_opponent_options = 0
    for col in valid_locations:
        row = get_next_open_row(board, col)
        temp_board = board.copy()
        drop_piece(temp_board, col, opponent)
        opponent_options = len(get_valid_locations(temp_board))
        if opponent_options > max_opponent_options:
            max_opponent_options = opponent_options

    return max_player_options * 10 - max_opponent_options * 5

def evaluate_board(board, player, depth, series_depth, series_count, win_count):
    score = 0

    # Heuristic 1: Ranks a board based on whether it's a win, loss, or immediate opponent victory setup
    if winning_move(board, PLAYER_X):
        return -1000  # Opponent (PLAYER_X) wins
    elif winning_move(board, PLAYER_O):
        return 1000  # Bot (PLAYER_O) wins

    # Heuristic 2: Depth of the board within the MinMax tree
    score += depth

    # Heuristic 3: Number of series of three consecutive tokens
    score += series_count * 10

    # Heuristic 4: Consider the depths of the series of three tokens
    score += series_depth

    # Heuristic 5: Give value to the number of wins a board has
    score += win_count * 100

    # Dodajemo ocenu za zatvaranje
    score += evaluate_closing(board, player)

    return score

def is_terminal_node(board):
    return (winning_move(board, PLAYER_X) or 
            winning_move(board, PLAYER_O) or 
            len(get_valid_locations(board)) == 0)

def winning_move(board, piece):
    # Check horizontal
    for col in range(COLS - 3):
        for row in range(ROWS):
            if board[row][col] == piece and board[row][col+1] == piece and board[row][col+2] == piece and board[row][col+3] == piece:
                return True

    # Check vertical
    for col in range(COLS):
        for row in range(ROWS - 3):
            if board[row][col] == piece and board[row+1][col] == piece and board[row+2][col] == piece and board[row+3][col] == piece:
                return True

    # Check positive diagonal
    for col in range(COLS - 3):
        for row in range(ROWS - 3):
            if board[row][col] == piece and board[row+1][col+1] == piece and board[row+2][col+2] == piece and board[row+3][col+3] == piece:
                return True

    # Check negative diagonal
    for col in range(COLS - 3):
        for row in range(3, ROWS):
            if board[row][col] == piece and board[row-1][col+1] == piece and board[row-2][col+2] == piece and board[row-3][col+3] == piece:
                return True

    return False

def get_valid_locations(board):
    valid_locations = []
    for col in range(COLS):
        if is_valid_move(board, col):
            valid_locations.append(col)
    return valid_locations

def Min(current_state, alpha, beta, depth, series_depth, series_count, win_count):
    if is_terminal_node(current_state) or depth == 0:
        return evaluate_board(current_state, PLAYER_X, depth, series_depth, series_count, win_count)

    min_eval = float('inf')

    for next_state in get_next_states(current_state, PLAYER_X):
        eval_val = Max(next_state, alpha, beta, depth - 1, series_depth, series_count, win_count)
        min_eval = min(min_eval, eval_val)
        beta = min(beta, eval_val)
        if beta <= alpha:
            break

    return min_eval

def Max(current_state, alpha, beta, depth, series_depth, series_count, win_count):
    if is_terminal_node(current_state) or depth == 0:
        return evaluate_board(current_state, PLAYER_O, depth, series_depth, series_count, win_count)

    max_eval = float('-inf')

    for next_state in get_next_states(current_state, PLAYER_O):
        eval_val = Min(next_state, alpha, beta, depth - 1, series_depth, series_count, win_count)
        max_eval = max(max_eval, eval_val)
        alpha = max(alpha, eval_val)
        if beta <= alpha:
            break

    return max_eval
    
def get_next_open_row(board, col):
    for r in range(ROWS-1, -1, -1):
        if board[r][col] == EMPTY:
            return r
    

def get_best_move(board, player, depth=3, series_depth=0, series_count=0, win_count=0):
    valid_locations = get_valid_locations(board)
    best_score = float('-inf')
    best_moves = []

    # Pokušaj da pronađemo optimalan potez, izbegavajući otvaranje u ćoškovima
    optimal_moves = list(set(valid_locations) - {0, COLS - 1})

    if len(optimal_moves) > 0:
        best_moves.append(random.choice(optimal_moves))
    else:
        best_moves.append(random.choice(valid_locations))

    for col in valid_locations:
        row = get_next_open_row(board, col)
        temp_board = board.copy()
        drop_piece(temp_board, col, player)
        score = Min(temp_board, float('-inf'), float('inf'), depth, series_depth, series_count, win_count)

        if score > best_score:
            best_moves = [col]
            best_score = score
        elif score == best_score:
            best_moves.append(col)

    # Random izbor između najboljih poteza
    return random.choice(best_moves)


if __name__ == '__main__':
    board = create_board()
    game_over = False
    turn = 0

    while not game_over:
        print(board)
        
        if turn == 0:
            # Bot's turn (Player O)
            col = get_best_move(board, PLAYER_O)
            row = get_next_open_row(board, col)
            drop_piece(board, col, PLAYER_O)
            if winning_move(board, PLAYER_O):
                print(board)
                print("Player O wins!")
                game_over = True
            turn += 1
        else:
            # Human player's turn (Player X)
            col = int(input("Your turn (0-7): "))
            if 0 <= col < COLS and is_valid_move(board, col):
                row = get_next_open_row(board, col)
                drop_piece(board, col, PLAYER_X)
                if winning_move(board, PLAYER_X):
                    print(board)
                    print("You win!")
                    game_over = True
                turn -= 1
            else:
                print("Invalid move! Try again.")

        if len(get_valid_locations(board)) == 0:
            print(board)
            print("It's a tie!")
            game_over = True

