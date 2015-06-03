package com.rhapsodyman.learndroid;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;

public class ParseExecute {
	private ConnectedThread connection;
	private String speed;
	private StringBuilder result = new StringBuilder();
	
	
	
	public ParseExecute(ConnectedThread conn, String speed){
		this.connection = conn; 
		this.speed  = speed;
		
	}

	public String parse(String source) {
		int index = source.indexOf("repeat");
		if (index == -1)
			result.append(source);
		else {
			int currentindex = 0;
			while (currentindex < source.length()) {
				String source2 = source
						.substring(currentindex, source.length());
				int newline = source2.indexOf("\n");
				String temp = source2.substring(0, newline); // single line to parse

				if (temp.startsWith("rep")) {
					int numberstart = temp.indexOf(" ") + 1;
					int count = Integer.parseInt(temp.substring(numberstart,
							newline));

					// need to know this repeat brace end index

					int braceind = braceend(source2, newline + 3);
					String inner = source2.substring(newline + 3, braceind);

					for (int i = 0; i < count; i++) {
						parse(inner);

					}
					currentindex = currentindex + braceind + 2; // need to do
																// offset

				} else {
					result.append(temp + "\n");
					currentindex = currentindex + newline + 1;
				}
			}

		}
		return result.toString();

	}

	public int braceend(String source, int index) {
		int sum = -1;
		char[] array = source.toCharArray();

		int i = 0;
		for (i = index; i < array.length; i++) {
			if (array[i] == '{')
				sum -= 1;
			if (array[i] == '}')
				sum += 1;
			if (sum == 0)
				break;

		}
		return i;

	}

	public void execute(String source) {
		String[] arr = source.split("\n");
		for (int i = 0; i < arr.length; i++) {
			int commandend = arr[i].indexOf(" ");
			String command = arr[i].substring(0, commandend);
			int count = Integer.parseInt(arr[i].substring(commandend + 1,
					arr[i].length()));
			if (command.equals("left"))
				connection.write(String.format("L140R%sw", speed));
					
			else if (command.equals("right")) {
				connection.write(String.format("L%sR140w", speed));
			} else if (command.equals("forward")) {
				connection.write(String.format("L%sR%sw", speed, speed));

			} else if (command.equals("backward")) {
				connection.write(String.format("L-%sR-%sw", speed, speed));
			}
			
			try {
				Thread.sleep(1000* count);
			} catch (InterruptedException e) {
				
			}
			
		}
		connection.write("L0R0w");

	}

}
