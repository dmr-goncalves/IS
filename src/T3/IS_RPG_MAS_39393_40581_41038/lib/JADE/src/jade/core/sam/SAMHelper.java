/** ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.  *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 **************************************************************** */
package jade.core.sam;

//#DOTNET_EXCLUDE_FILE
import jade.core.ServiceHelper;

/**
 * Helper interface for the System Activity Monitoring (SAM) Service.<br>
 * The SAM Service allows collecting system activity information such as the
 * average time to perform a given operations or the number of events of a given
 * type. When activated, the SAM service is used internally by the JADE runtime
 * to monitor entities related to JADE specific activities such as the average
 * message delivery time or the number of posted messages, but can be used by
 * application agents as well to monitor entities related to application
 * specific activities. In order to do that an agent must retrieve the
 * <code>SAMHelper</code> by means of the <code>getHelper()</code> method of the
 * <code>Agent</code> class and register providers of measures for the entities
 * that have to be monitored. The underlying SAM Service periodically invokes
 * such providers and collects all retrieved information in the Main Container
 * aggregating them properly. Many providers can be registered (possibly in
 * different containers) for the same entity. In general measures of the same
 * entity generated by different providers are mediated to compute an average
 * measure. Counters of events are treated differently: contributions from
 * different providers associated to the same counter are summed to compute a
 * total value.
 *
 * @see jade.core.Agent#getHelper(String)
 *
 */
public interface SAMHelper extends ServiceHelper {

    public static final String SERVICE_NAME = "jade.core.sam.SAM";

    /**
     * Register a provider of measures for a given entity
     *
     * @param entityName The name of the entity the registered provider will
     * provide measures for
     * @param provider The provider of measures
     * @see MeasureProvider
     */
    void addEntityMeasureProvider(String entityName, MeasureProvider provider);

    /**
     * Register a provider of average measures for a given entity
     *
     * @param entityName The name of the entity the registered provider will
     * provide measures for
     * @param provider The provider of average measures
     * @see AverageMeasureProvider
     * @see AverageMeasureProviderImpl
     */
    void addEntityMeasureProvider(String entityName, AverageMeasureProvider provider);

    /**
     * Register a provider to get values of a given counter
     *
     * @param counterName The name of the counter the registered provider will
     * get values of
     * @param provider The provider of counter values
     * @see CounterValueProvider
     */
    void addCounterValueProvider(String counterName, CounterValueProvider provider);

    /**
     * Register a new handler for SAM information collected in the Main
     * Container. This method has no effect if called on a peripheral container
     *
     * @param handler The new handler to be added.
     * @param first A boolean indication specifying whether the new handler must
     * be inserted at the beginning or at the end of the list of handlers.
     */
    void addHandler(SAMInfoHandler handler, boolean first);

    /**
     * Remove a handler for SAM information collected in the Main Container.
     * This method has no effect if called on a peripheral container
     *
     * @param handler The handler to be removed.
     */
    void removeHandler(SAMInfoHandler handler);
}