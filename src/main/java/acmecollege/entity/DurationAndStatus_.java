package acmecollege.entity;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-07-23T10:45:00.595-0400")
@StaticMetamodel(DurationAndStatus.class)
public class DurationAndStatus_ {
	public static volatile SingularAttribute<DurationAndStatus, LocalDateTime> startDate;
	public static volatile SingularAttribute<DurationAndStatus, LocalDateTime> endDate;
	public static volatile SingularAttribute<DurationAndStatus, Byte> active;
}
