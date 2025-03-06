# Secret Santa Application

## Added Dependencies:
1. **Spring Batch**: For managing batch processing.
2. **Redis Cache**: For caching and storing the Secret Santa assignments.
3. **Spring Data JPA**: For interacting with databases (if necessary for other operations).
4. **Spring Web**: For creating RESTful web services.
5. **CSV Writer**: For generating CSV files.

## How to Run the Application

### URL: `http://localhost:8190/upload/secretSanta`

This URL accepts a file as input.

- **File Validation**: The file should follow the format `companyName_year.csv`.
- **Batch Job**: When the file is uploaded, it triggers the batch job named `secretSantaJob`.
- **Processing**:
  - The batch job reads the file and processes the data.
  - The data is saved to Redis with the **bucket name** as `secret-santa`, and the **key** as the **year**. The value stored in Redis is a **map** where the **key** is `EmployeeEmailID` and the **value** is `SecretChildEmailID`.

### URL: `http://localhost:8190/upload/employee`

This URL also accepts a file as input.

- **File Validation**: The file should follow the format `companyName_year.csv`.
- **Batch Job**: When the file is uploaded, it triggers the batch job named `employeeJob`.
- **Processing**:
  - The employee list is retrieved from the file.
  - A random index of the employee emails is selected to assign a secret child email ID.
  - The batch job checks if the selected employee's `SecretChildEmailID` was used in the previous year. If it’s the first time assigning Secret Santa, it handles the case accordingly.
  - Using a CSV writer, the assignments are written into a CSV file in the expected format and saved under the **`src/resources`** directory. 

- **Result**: The generated CSV file will be saved under the `src/resources/employee/` directory, and it will contain the list of Secret Santa assignments for the employees. The file will be named with a unique identifier (based on the date and time of the upload) to ensure it is unique and doesn't overwrite previous files.


### URL: `http://localhost:8190/upload/secret-santa/{id}`

This API call expects an **ID** (which is the year) as a path variable. It deletes the specific Secret Santa data for that year from Redis.

- **Purpose**: This is mainly used for testing to clear data associated with a particular year from Redis.

### URL: `http://localhost:8190/upload/secret-santa`

This API retrieves all the records stored in Redis cache for Secret Santa.

- **Purpose**: This is used to fetch and view all the records saved in Redis.

