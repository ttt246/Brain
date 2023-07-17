import Foundation

/**
 * A class to send requests to Brain.
 */
class NetworkService {
    /**
     * This const varaibles represents all possible urls.
     */
    private let BASE_URL = "https://ttt246-brain.hf.space/"
    private let URL_SEND_NOTIFICATION = "sendNotification"
    private let URL_GET_CONTACTS_BY_IDS = "contacts/get_by_ids"
    
    
    /**
     * This function is used to get completed urls based on server url.
     */
    private func getRequestUrl(subUrl: String) -> String {
        return BASE_URL + subUrl
    }
    
    /**
     * A function to send a user's message to analyze by Brain.
     *
     * @param message This is a string variable that a user is going to send.
     * @param conf This is object that contains essential key values for switching backend
     * @param comopletion This is a callback function called when received response from backend
     */
    func sendNotification(string message: String, object conf: [String: Any], completion: @escaping (Result<CommandRes, Error>) -> Void) {
        if let url = URL(string: getRequestUrl(subUrl: URL_SEND_NOTIFICATION)) {
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            let postData: [String: Any] = ["confs": conf, "message": message]
            
            do {
                let jsonPostData = try JSONSerialization.data(withJSONObject: postData, options: .prettyPrinted)
                URLSession.shared.uploadTask(with: request, from: jsonPostData) { data, response, error in
                    if let error = error {
                        completion(.failure(error))
                        return
                    }
                    if let data = data {
                        let decoder = JSONDecoder()
                        do {
                            let notification = try decoder.decode(CommandRes.self, from: data)
                            completion(.success(notification))
                        } catch {
                            completion(.failure(error))
                        }
                    }
                }.resume()
            } catch {
                print("Error: \(error.localizedDescription)")
            }
        }
    }
    
    /**
     * A function to send a user's message to analyze by Brain
     *
     * @param contactIds This is a array variable that includes contact ids, which a user is going to get
     * @param conf This is object that contains essential key values for switching backend
     * @param comopletion This is a callback function called when received response from backend
     */
    func searchContacts(array contactIds: Array<String>, object conf: [String: Any], completion: @escaping (Result<ContactRes, Error>) -> Void) {
        if let url = URL(string: getRequestUrl(subUrl: URL_GET_CONTACTS_BY_IDS)) {
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            let postData: [String: Any] = ["confs": conf, "contactIds": contactIds]
            
            do {
                let jsonPostData = try JSONSerialization.data(withJSONObject: postData, options: .prettyPrinted)
                URLSession.shared.uploadTask(with: request, from: jsonPostData) { data, response, error in
                    if let error = error {
                        completion(.failure(error))
                        return
                    }
                    if let data = data {
                        let decoder = JSONDecoder()
                        do {
                            let contacts = try decoder.decode(ContactRes.self, from: data)
                            completion(.success(contacts))
                        } catch {
                            completion(.failure(error))
                        }
                    }
                }.resume()
            } catch {
                print("Error: \(error.localizedDescription)")
            }
        }
    }
}
