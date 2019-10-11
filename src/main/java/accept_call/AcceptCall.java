/* 
 * AFTER RUNNING PROJECT WITH COMMAND: 
 * `gradle build && java -Dserver.port=0080 -jar build/libs/gs-spring-boot-0.1.0.jar`
 * CALL NUMBER ASSOCIATED WITH THE ACCOUNT (CONFIGURED IN PERSEPHONY DASHBOARD)
 * EXPECT MESSAGE TO BE REPEATED TO YOU: 
 * 'Hello. Thank you for invoking the accept incoming call tutorial. Goodbye.'
*/

package main.java.accept_call;

import org.springframework.web.bind.annotation.RestController;
import com.vailsys.persephony.api.PersyException;
import com.vailsys.persephony.api.call.CallStatus;
import com.vailsys.persephony.percl.Hangup;
import com.vailsys.persephony.percl.Language;
import com.vailsys.persephony.percl.Pause;
import com.vailsys.persephony.percl.PerCLScript;
import com.vailsys.persephony.percl.Say;
import com.vailsys.persephony.webhooks.StatusCallback;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class AcceptCall {

  // To properly communicate with Persephony's API, set your Persephony app's
  // VoiceURL endpoint to '{yourApplicationURL}/InboundCall' for this example
  // Your Persephony app can be configured in the Persephony Dashboard
  @RequestMapping(value = {
      "/InboundCall" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<?> inboundCall(@RequestBody String str) {
    PerCLScript script = new PerCLScript();

    StatusCallback statusCallback;
    try {
      // Convert JSON into a call status callback object
      statusCallback = StatusCallback.fromJson(str);
    } catch (PersyException pe) {
      PerCLScript errorScript = new PerCLScript();
      Say sayError = new Say("There was a problem processing the incoming call.");
      sayError.setLanguage(Language.ENGLISH_US);
      errorScript.add(sayError);
      return new ResponseEntity<>(errorScript.toJson(), HttpStatus.OK);
    }

    if (statusCallback.getCallStatus() == CallStatus.RINGING) {
      // Create PerCL say script with US English as the language
      Say say = new Say("Hello. Thank you for invoking the accept incoming call tutorial.");
      say.setLanguage(Language.ENGLISH_US);

      // Add PerCL say script to PerCL container
      script.add(say);

      // Create PerCL pause script with a duration of 100 milliseconds
      Pause pause = new Pause(100);

      // Add PerCL pause script to PerCL container
      script.add(pause);

      // Create PerCL say script with US English as the language
      Say sayGoodbye = new Say("Goodbye.");
      sayGoodbye.setLanguage(Language.ENGLISH_US);

      // Add PerCL say script to PerCL container
      script.add(sayGoodbye);

      // Create PerCL hangup script
      Hangup hangup = new Hangup();

      // Add PerCL hangup script to PerCL container
      script.add(hangup);
    }

    // Convert PerCL container to JSON and append to response
    return new ResponseEntity<>(script.toJson(), HttpStatus.OK);
  }
}