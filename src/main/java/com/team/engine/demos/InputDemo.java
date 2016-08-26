package com.team.engine.demos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.team.engine.Engine;
import com.team.engine.Shader;

public class InputDemo extends Engine {
	
	static Socket socket;
	static DataOutputStream out;
	static BufferedReader in;
	static int port;
	
	private float accumulator = 0;
	private static final float REFRESH_RATE = 0.1f;
	
	public static void main(String[] args) {
		if (args.length != 1) {
			port = 5660;
		} else if (args[0].toString() == "help") {
			System.out.println("Usage: java RoboKalServer <port number>");
			System.exit(1);
		} else port = Integer.parseInt(args[0]);
		
		new InputDemo().initialize(false);
	}
	
	//private static Controller controller;

	@Override
	public void setupGame() {
		/*if (Controllers.getControllerCount() > 0) {
			controller = Controllers.getController(0);
		}*/
		
		System.out.println("Initializing socket...");
		try (ServerSocket serverSocket = new ServerSocket(port)) { 
			socket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port + ". Is it open?");
			System.exit(-1);
		}
		System.out.println("Socket initialized.");
		
		try {
			out = new DataOutputStream(socket.getOutputStream());
	        out.flush();
			in = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));
		} catch (Exception e) {}
	}
	
	@Override
	public void tick() {
		/*accumulator += Engine.instance.deltaTime;
		
		if (accumulator > REFRESH_RATE) {
			accumulator -= REFRESH_RATE;
			
			float lookX = controller.getAxisValue(1);
			float lookY = controller.getAxisValue(2);
			
			float keyX = 0;
			float keyY = 0;
			boolean useKey = false;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				keyX = 1.0f;
				useKey = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				keyX = -1.0f;
				useKey = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				keyY = -1.0f;
				useKey = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				keyY = 1.0f;
				useKey = true;
			}
			
			
			
			if (lookX > 0.3f || lookX < -0.3f || lookY > 0.3f || lookY < -0.3f || useKey) {
				System.out.print("Sending packet...");
				
				try {
					byte[] b;
					if (useKey) {
						b = ByteBuffer.allocate(8).putFloat(keyX).putFloat(keyY).array();
					}
					else {
						b = ByteBuffer.allocate(8).putFloat(lookX).putFloat(lookY).array();
					}
					out.write(b);
					out.flush();
				} catch (IOException e) {
					System.err.println("\nConnection to the client lost.\n");
					e.printStackTrace();
				}
				System.out.println(" Done.");
			}
		}*/
	}

	@Override
	public void render() {
		
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		
	}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		
		
	}
}
