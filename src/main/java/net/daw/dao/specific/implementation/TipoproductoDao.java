/*
 * Copyright (C) July 2014 Rafael Aznar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.daw.dao.specific.implementation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.daw.bean.specific.implementation.DocumentoBean;
import net.daw.bean.specific.implementation.ImpuestoBean;
import net.daw.bean.specific.implementation.TipoproductoBean;
import net.daw.dao.publicinterface.MetaDaoInterface;
import net.daw.dao.publicinterface.TableDaoInterface;
import net.daw.dao.publicinterface.ViewDaoInterface;
import net.daw.data.specific.implementation.MysqlDataSpImpl;
import net.daw.helper.statics.AppConfigurationHelper;
import net.daw.helper.statics.ExceptionBooster;
import net.daw.helper.statics.FilterBeanHelper;
import net.daw.helper.statics.SqlBuilder;

public class TipoproductoDao implements ViewDaoInterface<TipoproductoBean>,TableDaoInterface<TipoproductoBean>, MetaDaoInterface {

    private String strSqlSelectDataOrigin = null;
    private String strTableOrigin = null;
    private MysqlDataSpImpl oMysql = null;
    private Connection oConnection = null;

    public TipoproductoDao(Connection oConexion) throws Exception {
        try {
            strTableOrigin = "tipoproducto";
            strSqlSelectDataOrigin = "select * from " + strTableOrigin + " where 1=1 ";
            oConnection = oConexion;
            oMysql = new MysqlDataSpImpl(oConnection);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":constructor ERROR: " + ex.getMessage()));
        }
    }

    @Override
    public ArrayList<String> getColumnsNames() throws Exception {
        ArrayList<String> alColumns = null;
        try {
            alColumns = oMysql.getColumnsName(strTableOrigin);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getColumnsNames ERROR: " + ex.getMessage()));
        }
        return alColumns;
    }

    @Override
    public ArrayList<String> getPrettyColumnsNames() throws Exception {
        ArrayList<String> alColumns = null;
        try {
            alColumns = oMysql.getPrettyColumns(strTableOrigin);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPrettyColumnsNames ERROR: " + ex.getMessage()));
        }
        return alColumns;
    }

    @Override
    public int getPages(int intRegsPerPag, ArrayList<FilterBeanHelper> alFilter) throws Exception {
        strSqlSelectDataOrigin += SqlBuilder.buildSqlWhere(alFilter);
        int pages = 0;
        try {
            pages = oMysql.getNewPages(strSqlSelectDataOrigin, intRegsPerPag);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPages ERROR: " + ex.getMessage()));
        }
        return pages;
    }

    @Override
    public int getCount(ArrayList<FilterBeanHelper> alFilter) throws Exception {
        strSqlSelectDataOrigin += SqlBuilder.buildSqlWhere(alFilter);
        int pages = 0;
        try {
            pages = oMysql.getNewCount(strSqlSelectDataOrigin);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getCount ERROR: " + ex.getMessage()));
        }
        return pages;
    }

    @Override
    public ArrayList<TipoproductoBean> getPage(int intRegsPerPag, int intPage, ArrayList<FilterBeanHelper> alFilter, HashMap<String, String> hmOrder) throws Exception {
        strSqlSelectDataOrigin += SqlBuilder.buildSqlWhere(alFilter);
        strSqlSelectDataOrigin += SqlBuilder.buildSqlOrder(hmOrder);
        ArrayList<Integer> arrId;
        ArrayList<TipoproductoBean> arrTipoproducto = new ArrayList<>();
        try {
            arrId = oMysql.getNewPage(strSqlSelectDataOrigin, intRegsPerPag, intPage);
            Iterator<Integer> iterador = arrId.listIterator();
            while (iterador.hasNext()) {
                TipoproductoBean oTipoproductoBean = new TipoproductoBean(iterador.next());
                arrTipoproducto.add(this.get(oTipoproductoBean, AppConfigurationHelper.getJsonDepth()));
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPage ERROR: " + ex.getMessage()));
        }
        return arrTipoproducto;
    }

    @Override
    public TipoproductoBean get(TipoproductoBean oTipoproductoBean, Integer expand) throws Exception {
        if (oTipoproductoBean.getId() > 0) {
            try {
                if (!oMysql.existsNewOne(strSqlSelectDataOrigin, oTipoproductoBean.getId())) {
                    oTipoproductoBean.setId(0);
                } else {
                    expand--;
                    if (expand > 0) {
                        oTipoproductoBean.setDescripcion(oMysql.getNewOne(strSqlSelectDataOrigin, "descripcion", oTipoproductoBean.getId()));
                        oTipoproductoBean.setId_impuesto(Integer.parseInt(oMysql.getOne(strSqlSelectDataOrigin, "id_impuesto", oTipoproductoBean.getId())));

                        ImpuestoBean oImpuesto = new ImpuestoBean();
                        oImpuesto.setId(Integer.parseInt(oMysql.getNewOne(strSqlSelectDataOrigin, "id_impuesto", oTipoproductoBean.getId())));
                        ImpuestoDao oImpuestoDAO = new ImpuestoDao(oConnection);
                        oImpuesto = oImpuestoDAO.get(oImpuesto, AppConfigurationHelper.getJsonDepth());
                        oTipoproductoBean.setObj_impuesto(oImpuesto);
                    }
                }
            } catch (Exception ex) {
                ExceptionBooster.boost(new Exception(this.getClass().getName() + ":get ERROR: " + ex.getMessage()));
            }
        } else {
            oTipoproductoBean.setId(0);
        }
        return oTipoproductoBean;
    }

    @Override
    public TipoproductoBean set(TipoproductoBean oTipoproductoBean) throws Exception {
        try {
            if (oTipoproductoBean.getId() == 0) {
                oTipoproductoBean.setId(oMysql.insertOne(strTableOrigin));
            }
            oMysql.updateOne(oTipoproductoBean.getId(), strTableOrigin, "descripcion", oTipoproductoBean.getDescripcion());
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":set ERROR: " + ex.getMessage()));
        }
        return oTipoproductoBean;
    }

    @Override
    public int remove(TipoproductoBean oTipoproductoBean) throws Exception {
        int result = 0;
        try {
            result = oMysql.removeOne(oTipoproductoBean.getId(), strTableOrigin);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":remove ERROR: " + ex.getMessage()));
        }
        return result;
    }

}