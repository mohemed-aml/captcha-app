// scripts/import_captchas.js

const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');

// Configuration
const CAPTCHAS_DIR = '/home/amal/AndroidStudioProjects/CaptchApp/backend/captcha_images';
const DB_HOST = 'localhost';
const DB_USER = 'captchauser';
const DB_PASSWORD = 'yourpassword';
const DB_NAME = 'captchadb';

async function importCaptchas() {
    try {
        // Connect to the database
        const connection = await mysql.createConnection({
            host: DB_HOST,
            user: DB_USER,
            password: DB_PASSWORD,
            database: DB_NAME,
        });

        // Read all files in the CAPTCHA directory
        const files = fs.readdirSync(CAPTCHAS_DIR);

        // Filter image files (assuming .png)
        const imageFiles = files.filter(file => path.extname(file).toLowerCase() === '.png');

        for (const file of imageFiles) {
            const answer = path.basename(file, '.png'); // Extract answer from filename

            // Insert into the database
            await connection.execute(
                'INSERT INTO captchas (image_filename, answer) VALUES (?, ?)',
                [file, answer]
            );

            console.log(`Imported: ${file} with answer: ${answer}`);
        }

        // Close the connection
        await connection.end();
        console.log('Import completed successfully.');
    } catch (error) {
        console.error('Error importing captchas:', error);
    }
}

importCaptchas();