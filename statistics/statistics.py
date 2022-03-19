import os, sys, subprocess


def get_output(scenario: int) -> str:
	data, temp = os.pipe()
	os.write(temp, bytes(f"\n{scenario}\n", "utf-8"))
	os.close(temp)
	output = subprocess.check_output('java /home/yesliesnayder/projects/IdeaProjects/AIBookFinding/src/AndreyKuzmickiy.java', stdin=data, shell=True)
	return output.decode('utf-8').split('\n')
	


def check_statistics(scenario: int, count: int):
	counter = 0	# How many times program was called
	b_winrate = 0	# How many times Backtracking won
	a_winrate = 0	# How many times A star won
	loses = 0	# How many times Harry loss by poor field
	
	b_min_time = None	# Minimal working time for Backtracking
	a_min_time = None	# Minimal working time for A star
	b_max_time = 0		# Maximum working time for Backtracking
	a_max_time = 0		# Maximum working time for A star
	b_time = 0	    	# Working time for Backtracking
	a_time = 0	    	# Working time for A star

	b_min_steps = None	# Minimal number of steps for Backtracking
	a_min_steps = None	# Minimal number of steps for A star
	b_max_steps = 0		# Maximum number of steps for Backtracking
	a_max_steps = 0		# Maximum number of steps for A star
	b_steps = 0	    	# Number of steps for Backtracking
	a_steps = 0	    	# Number of steps for A star

	
	for i in range(count):
		counter += 1
		output = get_output(scenario)
		if output[11] == 'Loss due to wrong field':
			loses += 1
			continue
		if output[12] == 'Lose!':
			a_starting_point = 15
		elif output[12] == 'Win!':
			a_starting_point = 27
			b_winrate += 1
			steps = int(output[13].split(' ')[-1])
			b_steps += steps
			if b_min_steps is None or b_min_steps > steps:
				b_min_steps = steps
			if b_max_steps is None or b_max_steps < steps:
				b_max_steps = steps
			t = int(output[24].split(' ')[-2])
			b_time += t
			if b_min_time is None or b_min_time > t:
				b_min_time = t
			if b_max_time is None or b_max_time < t:
				b_max_time = t
		if output[a_starting_point] == 'Win!':
			a_winrate += 1
			steps = int(output[a_starting_point+1].split(' ')[-1])
			a_steps += steps
			if a_min_steps is None or a_min_steps > steps:
				a_min_steps = steps
			if a_max_steps is None or a_max_steps < steps:
				a_max_steps = steps
		t = int(output[a_starting_point+12].split(' ')[-2])
		a_time += t
		if a_min_time is None or a_min_time > t:
			a_min_time = t
		if a_max_time is None or a_max_time < t:
			a_max_time = t
		
	print(f'Counter: {counter}, Loses: {loses}, Loss rate: {(counter-loses) / counter}')
	print('-----   Backtracking   -----')
	print(f'Win: {b_winrate}, Lose: {counter-loses-b_winrate}, Win rate: {b_winrate / (counter-loses)}')
	print(f'Average time: {b_time / (counter-loses)} ms, Min time: {b_min_time} ms, Max time: {b_max_time} ms')
	print(f'Average steps: {b_steps / (counter-loses)}, Min steps: {b_min_steps}, Max steps: {b_max_steps}')
	print('--------   A star   --------')
	print(f'Win: {a_winrate}, Lose: {counter-loses-a_winrate}, Win rate: {a_winrate / (counter-loses)}')
	print(f'Average time: {a_time / (counter-loses)} ms, Min time: {a_min_time} ms, Max time: {a_max_time} ms')
	print(f'Average steps: {a_steps / (counter-loses)}, Min steps: {a_min_steps}, Max steps: {a_max_steps}')


if __name__ == '__main__':
	if len(sys.argv) == 1:
		c1, c2 = 10, 10
	elif len(sys.argv) == 2:
		c1, c2 = int(sys.argv[1]), int(sys.argv[1])
	elif len(sys.argv) == 3:
		c1, c2 = int(sys.argv[1]), int(sys.argv[2])
	else:
		print('Incorrect params')

	print('----------     The first scenario     ----------')
	check_statistics(1, c1)
	print()
	print('----------     The second scenario     ---------')
	check_statistics(2, c2)

