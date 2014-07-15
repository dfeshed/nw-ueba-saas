package fortscale.services.networksummary;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;

@Service("networkSummaryService")
public class NetworkSummaryService {
	@Autowired
	private ComputerRepository computerRepository;
	@Autowired
	private UserRepository userRepository;
	
	final private static int historyPeriodInHours = 24;
	private DateTime time;
	private DateTime previousTime;	
	
	public NetworkSummaryService() {
		time = DateTime.now();
		previousTime = time.minusHours(historyPeriodInHours);
	}

	public Map<String, NetworkSummaryDTO> getNetworkSummary(){
		HashMap<String, NetworkSummaryDTO> res = new HashMap<>();
		
		//machines summary
		long machinesNum = computerRepository.count();
		long prevMachinesNum = computerRepository.getNumberOfMachinesBeforeTime(previousTime);
		res.put("machines", new NetworkSummaryDTO(machinesNum, prevMachinesNum));
		
		//desktop summary
		long desktopsNum = computerRepository.getNumberOfMachinesOfType(ComputerUsageType.Desktop);
		long prevDesktopsNum = computerRepository.getNumberOfMachinesOfTypeBeforeTime(ComputerUsageType.Desktop, previousTime);
		res.put("endPoints", new NetworkSummaryDTO(desktopsNum, prevDesktopsNum));
		
		//server summary
		long serversNum = computerRepository.getNumberOfMachinesOfType(ComputerUsageType.Server);
		long prevServersNum = computerRepository.getNumberOfMachinesOfTypeBeforeTime(ComputerUsageType.Server, previousTime);
		res.put("servers", new NetworkSummaryDTO(serversNum, prevServersNum));
		
		//sensitive machines summary
		long sensitiveMachinesNum = computerRepository.getNumberOfSensitiveMachines();
		long prevSensitiveMachinesNum = computerRepository.getNumberOfSensitiveMachinesBeforeTime(previousTime);
		res.put("sensitiveMachines", new NetworkSummaryDTO(sensitiveMachinesNum, prevSensitiveMachinesNum));
		
		//accounts summary
		long accountsNum = userRepository.count();
		long prevAccountsNum = userRepository.getNumberOfAccountsCreatedBefore(previousTime);
		res.put("accounts", new NetworkSummaryDTO(accountsNum, prevAccountsNum));
		
		//disabled accounts summary
		long disabledAccountsNum = userRepository.getNumberOfDisabledAccounts();
		long prevDisabledAccountsNum = userRepository.getNumberOfDisabledAccountsBeforeTime(previousTime);
		res.put("disabledAccounts", new NetworkSummaryDTO(disabledAccountsNum, prevDisabledAccountsNum));

		//inactive accounts summary
		long inactiveAccountsNum = userRepository.getNumberOfInactiveAccounts();
		res.put("inactiveAccounts", new NetworkSummaryDTO(inactiveAccountsNum,-1));
		
		return res;
	}
	
	
	
	
}
