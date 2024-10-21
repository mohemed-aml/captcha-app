require('dotenv').config();
const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');

// Configuration
const MAPPING_FILE = path.join(__dirname, 'captcha_mapping.json');
const DB_HOST = process.env.DB_HOST;
const DB_USER = process.env.DB_USER;
const DB_PASSWORD = process.env.DB_PASSWORD;
const DB_NAME = process.env.DB_NAME;

async function importCaptchas() {
    try {
        // Connect to the database
        const connection = await mysql.createConnection({
            host: DB_HOST,
            user: DB_USER,
            password: DB_PASSWORD,
            database: DB_NAME,
            ssl: {
                ca: '/etc/ssl/certs/rds-combined-ca-bundle.pem'
            }
        });

        // Read the mapping file
        const data = fs.readFileSync(MAPPING_FILE, 'utf-8');
        const mappings = JSON.parse(data);

        for (const mapping of mappings) {
            const { image_filename, answer } = mapping;

            // Insert into the database
            await connection.execute(
                'INSERT INTO captchas (image_filename, answer) VALUES (?, ?)',
                [image_filename, answer]
            );

            console.log(`Imported: ${image_filename} with answer: ${answer}`);
        }

        // Close the connection
        await connection.end();
        console.log('Import completed successfully.');
    } catch (error) {
        console.error('Error importing captchas:', error);
    }
}

importCaptchas();