package architecture.community.web.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import architecture.community.query.ParameterValue;

public class DataSourceRequest {

	private int page;

	private int pageSize;

	private int take;

	private int skip;

	private FilterDescriptor filter;
	
	private List<SortDescriptor> sort;
	
	private List<GroupDescriptor> group;
	
    private List<AggregateDescriptor> aggregate;
    
	private HashMap<String, Object> data;
	
    private List<ParameterValue> parameters;
    
    private String statement;
	
	public DataSourceRequest() {
		filter = new FilterDescriptor();
		data = new HashMap<String, Object>();
		page = 0;
		pageSize = 0;
		take = 0;
		skip = 0; 
		statement = null;
		parameters = new ArrayList<ParameterValue>();
	}

	public List<SortDescriptor> getSort() {
		if( sort == null )
			return Collections.EMPTY_LIST;
        return sort;
    }

    public void setSort(List<SortDescriptor> sort) {
        this.sort = sort;
    }
    
    private List<SortDescriptor> sortDescriptors() {
        List<SortDescriptor> sort = new ArrayList<SortDescriptor>();
        List<GroupDescriptor> groups = getGroup();
        List<SortDescriptor> sorts = getSort();
        if (groups != null) {
            sort.addAll(groups);
        }                
        if (sorts != null) {
            sort.addAll(sorts);
        }
        return sort;        
    }
    
    public List<GroupDescriptor> getGroup() {
		if( group == null )
			return Collections.EMPTY_LIST;
        return group;
    }

    public void setGroup(List<GroupDescriptor> group) {
        this.group = group;
    }
    
    public List<AggregateDescriptor> getAggregate() {
		if( aggregate == null )
			return Collections.EMPTY_LIST;
        return aggregate;
    }

    public void setAggregate(List<AggregateDescriptor> aggregate) {
        this.aggregate = aggregate;
    }
    
	public FilterDescriptor getFilter() {
		return filter;
	}

	public void setFilter(FilterDescriptor filter) {
		this.filter = filter;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTake() {
		return take;
	}

	public void setTake(int take) {
		this.take = take;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public String getStatement() {
		return statement;
	}


	public void setStatement(String statement) {
		this.statement = statement;
	}


	public List<ParameterValue> getParameters() {
		return parameters;
	}


	public void setParameters(List<ParameterValue> parameters) {
		this.parameters = parameters;
	}


	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
		data.put(key, value);
	}

	public HashMap<String, Object> getData() {
		return data;
	}

	@JsonIgnore
	public void setData(String key, Object value) {
		data.put(key, value);
	}
	
	public String getDataAsString(String key, String defaultValue) {
		if (data.containsKey(key)) {
			try {
				return data.get(key).toString();
			} catch (Exception ignore) {
			}
		}
		return defaultValue;
	}

	public Long getDataAsLong(String key, Long defaultValue) {
		if (data.containsKey(key)) {
			try {
				Object value = data.get(key);
				if( value instanceof Long )
					return (Long)value;
				
				return Long.parseLong(value.toString());
			} catch (Exception ignore) {
			}
		}
		return defaultValue;
	}
	
	public Integer getDataAsInteger(String key, Integer defaultValue) {
		if (data.containsKey(key)) {
			try {
				Object value = data.get(key);
				if( value instanceof Integer )
					return (Integer)value;
				return Integer.parseInt(value.toString());
			} catch (Exception ignore) {
			}
		}
		return defaultValue;
	}

	public Boolean getDataAsBoolean(String key, Boolean defaultValue) {
		if (data.containsKey(key)) {
			try {
				return Boolean.parseBoolean(data.get(key).toString());
			} catch (Exception ignore) {
			}
		}
		return defaultValue;
	}
	

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("DataSourceRequest [page=").append(page).append(", pageSize=").append(pageSize).append(", take=")
				.append(take).append(", skip=").append(skip).append(", ");
		if (data != null)
			builder.append("data=").append(toString(data.entrySet(), maxLen)).append(", ");
		if (filter != null)
			builder.append("filter=").append(filter).append(", ");
		if (sort != null)
			builder.append("sort=").append(toString(sort, maxLen)).append(", ");
		if (group != null)
			builder.append("group=").append(toString(group, maxLen)).append(", ");
		if (aggregate != null)
			builder.append("aggregate=").append(toString(aggregate, maxLen));
		builder.append("]");
		return builder.toString();
	}


	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}


	public static class FilterDescriptor {
		
		private String logic;
		
		private List<FilterDescriptor> filters;
		
		private String field;
		
		private Object value;
		
		private String operator;
		
		private boolean ignoreCase = true;

		public FilterDescriptor() {
			filters = new ArrayList<FilterDescriptor>();
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getLogic() {
			return logic;
		}

		public void setLogic(String logic) {
			this.logic = logic;
		}

		public boolean isIgnoreCase() {
			return ignoreCase;
		}

		public void setIgnoreCase(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}

		public List<FilterDescriptor> getFilters() {
			return filters;
		}

		@Override
		public String toString() {
			return "FilterDescriptor [logic=" + logic + ", filters=" + filters + ", field=" + field + ", value=" + value
					+ ", operator=" + operator + ", ignoreCase=" + ignoreCase + "]";
		}

	}
	

	
	
	public static class SortDescriptor {
		
		private String field;
		private String dir;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		@Override
		public String toString() {
			return "SortDescriptor [field=" + field + ", dir=" + dir + "]";
		}

	}

	public static class GroupDescriptor extends SortDescriptor {
		private List<AggregateDescriptor> aggregates;

		public GroupDescriptor() {
			aggregates = new ArrayList<AggregateDescriptor>();
		}

		public List<AggregateDescriptor> getAggregates() {
			return aggregates;
		}

		@Override
		public String toString() {
			return "GroupDescriptor [aggregates=" + aggregates + "]";
		}

	}

	public static class AggregateDescriptor {
		private String field;
		private String aggregate;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getAggregate() {
			return aggregate;
		}

		public void setAggregate(String aggregate) {
			this.aggregate = aggregate;
		}

		@Override
		public String toString() {
			return "AggregateDescriptor [field=" + field + ", aggregate=" + aggregate + "]";
		}

	}
}
