package ke.co.esuite.db.persistence;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import ke.co.esuite.config.UseDatasourceDb;
import ke.co.esuite.db.persistence.domain.ConfirmationData;
import ke.co.esuite.db.persistence.domain.RegisterUrlData;
import ke.co.esuite.db.persistence.domain.StkPushCallbackData;
import ke.co.esuite.db.persistence.domain.StkPushData;

@UseDatasourceDb
public interface DbMapper {
	public void saveMpesaConfirmation(ConfirmationData data);
	public void saveMpesaRegistrationUrls(RegisterUrlData data);
	public void saveStkPush(StkPushData data);
	
	public void updateStkPushCallback(StkPushCallbackData data);
	
	public List<ConfirmationData> retrieveConfirmation(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
