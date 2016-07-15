const WebSocketServer = require('ws').Server;
const keypress = require('keypress');

const wss = new WebSocketServer({ port: 9001 });

// Set the initial location
let lat = 53.7282337;
let long = -1.8642777;
let alt = 5;

// How much to move on each keypress
const speed = 0.0000100;

keypress(process.stdin);

wss.on('connection', ws => {
    console.log('Client connected');
    ws.on('close', () => process.exit(0));
    // Start listening to keypress on the terminal when connected
    process.stdin.on('keypress', (ch, key) => {
        console.log(`Got keypress: ${JSON.stringify(key)}`);
        // Exit on ctrl + c
        if(key && key.ctrl && key.name === 'c') {
            process.exit(0);
        }
        switch(key.name) {
            case 'up':
                lat += speed;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'down':
                lat -= speed;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'left':
                long -= speed;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'right':
                long += speed;
                ws.send(`${lat}:${long}:${alt}`);
                break;
        }
    });
});


process.stdin.setRawMode(true);
process.stdin.resume();
