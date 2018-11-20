/*
 * The MIT License
 *
 * Copyright (c) 2018 - present, Karl Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package jenkins.plugins.linkcolumn;

import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

public class LinkColumn extends ListViewColumn {

    private String columnName;
    private String linkName;
    private String link;

    @DataBoundConstructor
    public LinkColumn(String columnName, String linkName, String link) {
        super();
        this.columnName = columnName;
        this.linkName = linkName;
        this.link = link;
    }


    public String getColumnName() {
        return columnName;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLink() {
        return link;
    }


    public String getLinkNameWithToken(Job<?, ?> job) {
        return tokenize(linkName, job);
    }

    public String getLinkWithToken(Job<?, ?> job) {
        return tokenize(link, job);
    }

    private String tokenize(String value, Job<?, ?> job) {
        Run<?, ?> lastBuild = job.getLastBuild();
        String rootUrl = Jenkins.getInstance().getRootUrl();
        value = value.replace("${BUILD_URL}", lastBuild != null ? rootUrl + job.getShortUrl() + String.valueOf(lastBuild.getNumber()) + "/" : "null");
        value = value.replace("${BUILD_NUMBER}", lastBuild != null ? String.valueOf(lastBuild.getNumber()) : "null");
        value = value.replace("${BUILD_STATUS}", lastBuild != null && lastBuild.getResult() != null ? lastBuild.getResult().toString() : "null");
        value = value.replace("${JOB_NAME}", job.getName());
        value = value.replace("${JOB_URL}", rootUrl + job.getShortUrl());
        return value;
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        public DescriptorImpl() {
            Items.XSTREAM2.addCompatibilityAlias("hudson.views.LinkColumn", LinkColumn.class);
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Custom Link";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/link-column/link-column.html";
        }
    }

}
